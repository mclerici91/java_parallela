package S11.Es2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Coordinate {
    private final double lat;
    private final double lon;

    public Coordinate(final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    /**
     * Returns the distance (expressed in km) between two coordinates
     *
     * @param from
     * @return Returns the distance expressed in km
     */
    public double distance(final Coordinate from) {
        final double earthRadius = 6371.000; // km
        final double dLat = Math.toRadians(from.lat - this.lat);
        final double dLng = Math.toRadians(from.lon - this.lon);
        final double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(from.lat))
                * Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2.0) * Math.sin(dLng / 2.0);
        final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (earthRadius * c);
    }

    @Override
    public String toString() {
        return String.format("[%.5f, %.5f]", lat, lon);
    }
}

class Earthquake {
    private final static String CSV_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    private final Date time;
    private final Coordinate position;
    private final double depth;
    private final double magnitude;
    private final String place;

    public Earthquake(final Date time, final Coordinate pos, final double depth, final double mag, final String place) {
        this.time = time;
        this.position = pos;
        this.depth = depth;
        this.magnitude = mag;
        this.place = place;
    }

    public Coordinate getPosition() {
        return position;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getDepth() {
        return depth;
    }

    public static Earthquake parse(final String csvLine) {
        final String[] splits = csvLine.split(CSV_REGEX);
        if (splits.length != 15) {
            System.out.println("Failed to parse: " + csvLine);
            return null;
        }

        final Date time;

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            time = sdf.parse(splits[0]);
        } catch (final ParseException e) {
            return null;
        }

        final double lat = tryParseDouble(splits[1]);
        final double lon = tryParseDouble(splits[2]);
        final double depth = tryParseDouble(splits[3]);
        final double mag = tryParseDouble(splits[4]);
        final String place = splits[13];

        return new Earthquake(time, new Coordinate(lat, lon), depth, mag, place);
    }

    private static Double tryParseDouble(final String str) {
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException e) {
            return new Double(0);
        }
    }

    @Override
    public String toString() {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(time) + " mag: " + magnitude
                + " depth: " + depth + "km @ " + position + " " + place;
    }
}

public class S11Esercizio2 {

    private static List<Earthquake> loadEarthquakeDB(final String address, final boolean isLocalFile) {
        final List<Earthquake> quakes = new ArrayList<Earthquake>();

        final Reader reader;
        if (isLocalFile) {
            try {
                final File file = new File(address);
                reader = new FileReader(file);
            } catch (final FileNotFoundException e2) {
                System.out.println("Failed to open file: " + address);
                return Collections.emptyList();
            }
        } else {
            final URL url;
            try {
                url = new URL(address);
            } catch (final MalformedURLException e) {
                System.out.println("Failed to create URL for address: " + address);
                return Collections.emptyList();
            }
            final InputStream is;
            try {
                is = url.openStream();
            } catch (final IOException e) {
                System.out.println("Failed to open stream for: " + address);
                return Collections.emptyList();
            }
            reader = new InputStreamReader(is);
        }

        System.out.println("Requesting earthquake data from: " + address + " ...");

        String line;
        try {
            final BufferedReader br = new BufferedReader(reader);
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                final Earthquake quake = Earthquake.parse(line);
                if (quake != null)
                    quakes.add(quake);
                else
                    System.out.println("Failed to parse: " + line);
            }
            br.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return quakes;
    }


    /* ESTRAZIONE INFORMAZIONI - STREAMS */
    public static Earthquake sgetNearest(Stream<Earthquake> list, Coordinate supsi) {
        return list
                .min(Comparator.comparing(q -> q.getPosition().distance(supsi)))
                .get();
    }

    public static Earthquake sgetFurthest(Stream<Earthquake> list, Coordinate supsi) {
        return list
                .max(Comparator.comparing(q -> q.getPosition().distance(supsi)))
                .get();
    }

    public static Earthquake sgetStrongest(Stream<Earthquake> list, Coordinate supsi) {
        return list
                .max(Comparator.comparing(q -> q.getMagnitude()))
                .get();
    }

    public static List<Earthquake> sgetTenNearest(Stream<Earthquake> list, Coordinate supsi) {
        return list
                .filter(q -> q.getMagnitude() >= 4.0 && q.getMagnitude() <= 6.0 && q.getPosition().distance(supsi) >= 2000)
                .limit(10)
                .collect(Collectors.toList());
    }

    public static double sgetCountLat46 (Stream<Earthquake> list) {
        return list
                .filter(q -> q.getPosition().getLat() >= 46.0 && q.getPosition().getLat() < 47.0)
                .count();
    }

    public static double sgetCountLong8 (Stream<Earthquake> list) {
        return list
                .filter(q -> q.getPosition().getLon() >= 8.0 && q.getPosition().getLon() < 9.0)
                .count();
    }

    public static Map<Integer, Integer> sgetCountDepth (Stream<Earthquake> list) {
        return list
                .collect(Collectors.groupingBy(q -> (int) q.getDepth()/100))
                .entrySet().stream()
                .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue().size()));
    }

    public static Map<Integer, Integer> sgetCountMagnitude (Stream<Earthquake> list) {
        return list
                .collect(Collectors.groupingBy(q -> (int) q.getMagnitude()))
                .entrySet().stream()
                .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue().size()));
    }


    public static void main(final String[] args) {
        final String URI = "src/S11/Es1/2014-2015.csv";
        final long startTime = System.currentTimeMillis();

        final long computeTime = System.currentTimeMillis();

        final Coordinate supsi = new Coordinate(46.0234, 8.9172);

        final CompletableFuture<List<Earthquake>> cfQuakes = CompletableFuture
                .supplyAsync(() -> loadEarthquakeDB(URI, true));


        final CompletableFuture<Earthquake> nearestQuake = cfQuakes
                .thenApplyAsync(q -> sgetNearest(q.stream(), supsi));

        final CompletableFuture<Earthquake> furthestQuake = cfQuakes
                .thenApplyAsync(q -> sgetFurthest(q.stream(), supsi));

        final CompletableFuture<Earthquake> strongestQuake = cfQuakes
                .thenApplyAsync(q -> sgetStrongest(q.stream(), supsi));

        final CompletableFuture<List<Earthquake>> tenNearestQuakes = cfQuakes
                .thenApplyAsync(q -> sgetTenNearest(q.stream(), supsi));

        final CompletableFuture<Double> countLat46Quakes = cfQuakes
                .thenApplyAsync(q -> sgetCountLat46(q.stream()));

        final CompletableFuture<Double> countLong8Quakes = cfQuakes
                .thenApplyAsync(q -> sgetCountLong8(q.stream()));

        final CompletableFuture<Map<Integer, Integer>> countDepthQuakes = cfQuakes
                .thenApplyAsync(q -> sgetCountDepth(q.stream()));

        final CompletableFuture<Map<Integer, Integer>> countMagnitudeQuakes = cfQuakes
                .thenApplyAsync(q -> sgetCountMagnitude(q.stream()));

        try {
            cfQuakes.get();
            Earthquake nearest = nearestQuake.get();
            Earthquake furthest = furthestQuake.get();
            Earthquake strongest = strongestQuake.get();
            List<Earthquake> tenQuakes = tenNearestQuakes.get();
            Double lat46Quakes = countLat46Quakes.get();
            Double long8Quakes = countLong8Quakes.get();
            Map<Integer, Integer> depthQuakes = countDepthQuakes.get();
            Map<Integer, Integer> magnitudeQuakes = countMagnitudeQuakes.get();

            // Stampa il terremoto più vicino
            System.out.println("The nearest earthquake: " + nearest);

            //Stampa il terremoto più lontano
            System.out.println("The furthest earthquake: " + furthest);

            //Stampa il terremoto più forte
            System.out.println("The strongest earthquake: " + strongest);

            //Stampa 10 terremoti più vicini tra magnitudo 4 e 6 ad una distanza di almeno 2000 km
            for (Earthquake earthquake : tenQuakes) {
                System.out.println("The 10 earthquakes: " + earthquake);
            }

            //Stampa il numero di terremoti con latitudine 46
            System.out.println("The earthquake with latitude 46: " + lat46Quakes);

            //Stampa il numero di terremoti con longitudine 8
            System.out.println("The earthquake with longitude 8: " + long8Quakes);

            //Stampa il numero di terremoti per fasce di profondità di 100km
            for (Map.Entry<Integer, Integer> entry : depthQuakes.entrySet()) {
                System.out.println("Map earthquake with 100km depth: " + entry.getKey() + " / " + entry.getValue());
            }

            //Stampa il numero di terremoti per fasce di intensità
            for (Map.Entry<Integer, Integer> entry : magnitudeQuakes.entrySet()) {
                System.out.println("Map earthquake with magnitude: " + entry.getKey() + " / " + entry.getValue());
            }
        } catch (final InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        final long endTime = System.currentTimeMillis();
        System.out.println("Completed in " + ((endTime - startTime)) + " ms" + " (computation time=" + (endTime - computeTime) + " ms)");

        // Completed in 6291ms - computation time: 6291ms
    }
}
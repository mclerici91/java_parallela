package S11.Es1;

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
import java.util.stream.Collectors;

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

public class S11Esercizio1 {

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
    public static Earthquake sgetNearest(List<Earthquake> list, Coordinate supsi) {
        return list.stream()
                .min(Comparator.comparing(q -> q.getPosition().distance(supsi)))
                .get();
    }

    public static Earthquake sgetFurthest(List<Earthquake> list, Coordinate supsi) {
        return list.stream()
                .max(Comparator.comparing(q -> q.getPosition().distance(supsi)))
                .get();
    }

    public static Earthquake sgetStrongest(List<Earthquake> list, Coordinate supsi) {
        return list.stream()
                .max(Comparator.comparing(q -> q.getMagnitude()))
                .get();
    }

    public static List<Earthquake> sgetTenNearest(List<Earthquake> list, Coordinate supsi) {
        return list.stream()
                .filter(q -> q.getMagnitude() >= 4.0 && q.getMagnitude() <= 6.0 && q.getPosition().distance(supsi) >= 2000)
                .limit(10)
                .collect(Collectors.toList());
    }

    public static double sgetCountLat46 (List<Earthquake> list) {
        return list.stream()
                .filter(q -> q.getPosition().getLat() >= 46.0 && q.getPosition().getLat() < 47.0)
                .count();
    }

    public static double sgetCountLong8 (List<Earthquake> list) {
        return list.stream()
                .filter(q -> q.getPosition().getLon() >= 8.0 && q.getPosition().getLon() < 9.0)
                .count();
    }

    public static Map<Integer, Integer> sgetCountDepth (List<Earthquake> list) {
        return list.stream()
                .collect(Collectors.groupingBy(q -> (int) q.getDepth()/100))
                .entrySet().stream()
                .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue().size()));
    }

    public static Map<Integer, Integer> sgetCountMagnitude (List<Earthquake> list) {
        return list.stream()
                .collect(Collectors.groupingBy(q -> (int) q.getMagnitude()))
                .entrySet().stream()
                .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue().size()));
    }


    /* ESTRAZIONE INFORMAZIONI - PARALLEL STREAMS */
    public static Earthquake getNearest(List<Earthquake> list, Coordinate supsi) {
        return list.parallelStream()
                .min(Comparator.comparing(q -> q.getPosition().distance(supsi)))
                .get();
    }

    public static Earthquake getFurthest(List<Earthquake> list, Coordinate supsi) {
        return list.parallelStream()
                .max(Comparator.comparing(q -> q.getPosition().distance(supsi)))
                .get();
    }

    public static Earthquake getStrongest(List<Earthquake> list, Coordinate supsi) {
        return list.parallelStream()
                .max(Comparator.comparing(q -> q.getMagnitude()))
                .get();
    }

    public static List<Earthquake> getTenNearest(List<Earthquake> list, Coordinate supsi) {
        return list.parallelStream()
                .filter(q -> q.getMagnitude() >= 4.0 && q.getMagnitude() <= 6.0 && q.getPosition().distance(supsi) >= 2000)
                .limit(10)
                .collect(Collectors.toList());
    }

    public static double getCountLat46 (List<Earthquake> list) {
        return list.parallelStream()
                .filter(q -> q.getPosition().getLat() >= 46.0 && q.getPosition().getLat() < 47.0)
                .count();
    }

    public static double getCountLong8 (List<Earthquake> list) {
        return list.parallelStream()
                .filter(q -> q.getPosition().getLon() >= 8.0 && q.getPosition().getLon() < 9.0)
                .count();
    }

    public static Map<Integer, Integer> getCountDepth (List<Earthquake> list) {
        return list.parallelStream()
                .collect(Collectors.groupingBy(q -> (int) q.getDepth()/100))
                .entrySet().stream()
                .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue().size()));
    }

    public static Map<Integer, Integer> getCountMagnitude (List<Earthquake> list) {
        return list.parallelStream()
                .collect(Collectors.groupingBy(q -> (int) q.getMagnitude()))
                .entrySet().stream()
                .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue().size()));
    }

    public static void printStreamsResults (List<Earthquake> list, Coordinate supsi) {
        // Stampa il terremoto più vicino
        System.out.println("The nearest earthquake: " + sgetNearest(list, supsi));

        //Stampa il terremoto più lontano
        System.out.println("The furthest earthquake: " + sgetFurthest(list, supsi));

        //Stampa il terremoto più forte
        System.out.println("The strongest earthquake: " + sgetStrongest(list, supsi));

        //Stampa 10 terremoti più vicini tra magnitudo 4 e 6 ad una distanza di almeno 2000 km
        for (Earthquake earthquake : sgetTenNearest(list, supsi)) {
            System.out.println("The 10 earthquakes: " + earthquake);
        }

        //Stampa il numero di terremoti con latitudine 46
        System.out.println("The earthquake with latitude 46: " + sgetCountLat46(list));

        //Stampa il numero di terremoti con longitudine 8
        System.out.println("The earthquake with longitude 8: " + sgetCountLong8(list));

        //Stampa il numero di terremoti per fasce di profondità di 100km
        for (Map.Entry<Integer, Integer> entry : sgetCountDepth(list).entrySet()) {
            System.out.println("Map earthquake with 100km depth: " + entry.getKey() + " / " + entry.getValue());
        }

        //Stampa il numero di terremoti per fasce di intensità
        for (Map.Entry<Integer, Integer> entry : sgetCountMagnitude(list).entrySet()) {
            System.out.println("Map earthquake with magnitude: " + entry.getKey() + " / " + entry.getValue());
        }
    }

    public static void printParallelStreamsResults (List<Earthquake> list, Coordinate supsi) {
        // Stampa il terremoto più vicino
        System.out.println("The nearest earthquake: " + getNearest(list, supsi));

        //Stampa il terremoto più lontano
        System.out.println("The furthest earthquake: " + getFurthest(list, supsi));

        //Stampa il terremoto più forte
        System.out.println("The strongest earthquake: " + getStrongest(list, supsi));

        //Stampa 10 terremoti più vicini tra magnitudo 4 e 6 ad una distanza di almeno 2000 km
        for (Earthquake earthquake : getTenNearest(list, supsi)) {
            System.out.println("The 10 earthquakes: " + earthquake);
        }

        //Stampa il numero di terremoti con latitudine 46
        System.out.println("The earthquake with latitude 46: " + getCountLat46(list));

        //Stampa il numero di terremoti con longitudine 8
        System.out.println("The earthquake with longitude 8: " + getCountLong8(list));

        //Stampa il numero di terremoti per fasce di profondità di 100km
        for (Map.Entry<Integer, Integer> entry : getCountDepth(list).entrySet()) {
            System.out.println("Map earthquake with 100km depth: " + entry.getKey() + " / " + entry.getValue());
        }

        //Stampa il numero di terremoti per fasce di intensità
        for (Map.Entry<Integer, Integer> entry : getCountMagnitude(list).entrySet()) {
            System.out.println("Map earthquake with magnitude: " + entry.getKey() + " / " + entry.getValue());
        }
    }


    public static void main(final String[] args) {
        final String URI = "src/S11/Es1/2014-2015.csv";
        final long startTime = System.currentTimeMillis();

        final List<Earthquake> quakes = loadEarthquakeDB(URI, true);
        final long computeTime = System.currentTimeMillis();

        if (quakes.isEmpty()) {
            System.out.println("No earthquakes found!");
            return;
        }
        System.out.println("Loaded " + quakes.size() + " earthquakes");

        final Coordinate supsi = new Coordinate(46.0234, 8.9172);


        // Estrae le informazioni con l'uso di streams - completed in 6649ms, computation time: 648ms
        printStreamsResults(quakes, supsi);

        // Estrae le informazioni con l'uso di parallel streams - completed in 6432ms, computation time: 432ms
        printParallelStreamsResults(quakes, supsi);


        final long endTime = System.currentTimeMillis();
        System.out.println("Completed in " + ((endTime - startTime)) + " ms" + " (computation time=" + (endTime - computeTime) + " ms)");
    }
}
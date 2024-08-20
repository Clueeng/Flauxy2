package uwu.flauxy.utils;

import viamcp.protocols.ProtocolCollection;

import java.util.Arrays;
import java.util.Collections;

public class ViaUtil {

    public static ProtocolCollection[] values = ProtocolCollection.values();
    public static String toReadableVersion(int protocol){
        values = ProtocolCollection.values();
        Collections.reverse(Arrays.asList(values));
        float dragValue = (float) (ProtocolCollection.values().length - Arrays.asList(ProtocolCollection.values()).indexOf(ProtocolCollection.getProtocolCollectionById(protocol))) / ProtocolCollection.values().length;
        return values[(int) (dragValue * (values.length - 1))].getVersion().getName();
    }

}

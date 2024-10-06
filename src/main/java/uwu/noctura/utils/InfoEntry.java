package uwu.noctura.utils;

import java.util.function.Supplier;

public class InfoEntry {
    private final String label;
    private final Supplier<String> valueSupplier;

    public InfoEntry(String label, Supplier<String> valueSupplier) {
        this.label = label;
        this.valueSupplier = valueSupplier;
    }

    public String getFormattedText() {
        return label + " : " + valueSupplier.get();
    }
}
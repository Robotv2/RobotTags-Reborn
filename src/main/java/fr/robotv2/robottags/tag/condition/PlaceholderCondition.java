package fr.robotv2.robottags.tag.condition;

import fr.robotv2.robottags.tag.Tag;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public record PlaceholderCondition(String placeholder, Object required) implements TagCondition {

    private enum ValueType {
        NUMBER,
        STRING;
    }

    private ValueType getValueType(String formattedPlaceholder) {
        try {
            Double.parseDouble(formattedPlaceholder);
            return ValueType.NUMBER;
        } catch (NumberFormatException exception) {
            return ValueType.STRING;
        }
    }

    @Override
    public boolean meetCondition(Player player, Tag tag) {

        final String formattedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");
        final ValueType type = getValueType(formattedPlaceholder);

        try {
            switch (type) {
                case NUMBER -> {
                    double playerValue = Double.parseDouble(formattedPlaceholder);
                    double required = ((Number) this.required).doubleValue();
                    return playerValue >= required;
                }
                case STRING -> {
                    String required = (String) this.required;
                    return formattedPlaceholder.equalsIgnoreCase(required);
                }
            }
        } catch (ClassCastException ignored) {}
        return false;
    }
}

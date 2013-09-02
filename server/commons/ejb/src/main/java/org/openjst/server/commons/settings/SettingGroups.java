package org.openjst.server.commons.settings;

import org.openjst.server.commons.model.types.SettingValueType;
import org.openjst.server.commons.mq.model.AbstractModel;
import org.openjst.server.commons.services.SettingsManager;

import java.util.*;

/**
 * @author Sergey Grachev
 */
public final class SettingGroups {

    private final Map<String, Group> groups = new HashMap<String, Group>();

    public SettingGroups(final Object... objects) {
        for (final Object o : objects) {
            if (o instanceof Group) {
                final Group group = (Group) o;
                groups.put(group.getName(), group);
            }
        }
    }

    public Collection<Group> getGroups() {
        return groups.values();
    }

    public SettingGroups assignValues(final SettingsManager settingsManager) {
        for (final Group group : groups.values()) {
            group.assignValues(settingsManager);
        }
        return this;
    }

    public static Group group(final String name, final Object... objects) {
        final Group result = new Group(name);
        for (final Object o : objects) {
            if (o instanceof Section) {
                result.addSection((Section) o);
            }
        }
        return result;
    }

    public static Section section(final String name, final Object... objects) {
        final Section result = new Section(name);
        for (final Object o : objects) {
            if (o instanceof SectionSetting) {
                result.addSetting((SectionSetting) o);
            } else if (o instanceof Requires) {
                result.addRequires((Requires) o);
            }
        }
        return result;
    }

    public static SectionSetting setting(final Setting setting, final Object... objects) {
        final SectionSetting result = new SectionSetting(setting);
        for (final Object o : objects) {
            if (o instanceof Requires) {
                result.addRequires((Requires) o);
            }
        }
        return result;
    }

    public static Requires require(final Setting... settings) {
        return new Requires(settings);
    }

    public Group get(final String groupId) {
        return groups.get(groupId);
    }

    public Map<String, Setting> getSettingsMap() {
        return null;
    }

    public static final class Group extends AbstractModel implements Cloneable {

        private final String name;
        private Set<Section> sections;

        public Group(final String name) {
            this.name = name;
        }

        public void addSection(final Section section) {
            if (sections == null) {
                sections = new LinkedHashSet<Section>();
            }
            sections.add(section);
        }

        public String getName() {
            return name;
        }

        public Set<Section> getSections() {
            return sections;
        }

        @Override
        public Group clone() {
            try {
                return (Group) super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        public Group assignValues(final SettingsManager settingsManager) {
            for (final Section section : sections) {
                section.assignValues(settingsManager);
            }
            return this;
        }


        public Map<String, Setting> settings() {
            final Map<String, Setting> result = new LinkedHashMap<String, Setting>();
            for (final Section section : sections) {
                result.putAll(section.settings());
            }
            return result;
        }
    }

    public static final class Section extends AbstractModel {

        private final String name;
        private Set<SectionSetting> settings;
        private Set<String> requires;

        public Section(final String name) {
            this.name = name;
        }

        public void addSetting(final SectionSetting setting) {
            if (settings == null) {
                settings = new LinkedHashSet<SectionSetting>(1);
            }
            settings.add(setting);
        }

        public void addRequires(final Requires requires) {
            if (this.requires == null) {
                this.requires = new LinkedHashSet<String>();
            }
            this.requires.addAll(requires);
        }

        public String getName() {
            return name;
        }

        public Set<SectionSetting> getSettings() {
            return settings;
        }

        public Set<String> getRequires() {
            return requires;
        }

        public void assignValues(final SettingsManager settingsManager) {
            for (final SectionSetting setting : settings) {
                setting.setValue(settingsManager.get(setting.origin()));
            }
        }

        public Map<String, Setting> settings() {
            final Map<String, Setting> result = new LinkedHashMap<String, Setting>();
            for (final SectionSetting setting : settings) {
                result.put(setting.getName(), setting.origin());
            }
            return result;
        }
    }

    public static final class SectionSetting extends AbstractModel {

        private final Setting meta;
        private Set<String> requires;
        private Object value;

        public SectionSetting(final Setting setting) {
            meta = setting;
        }

        public void addRequires(final Requires requires) {
            if (this.requires == null) {
                this.requires = new LinkedHashSet<String>();
            }
            this.requires.addAll(requires);
        }

        public Setting origin() {
            return meta;
        }

        public Set<String> getRequires() {
            return requires;
        }

        public Object getValue() {
            return value;
        }

        public String getName() {
            return meta.key();
        }

        public SettingValueType getType() {
            return meta.type();
        }

        public Object getDefaultValue() {
            return meta.defaultValue();
        }

        public void setValue(final Object value) {
            this.value = value;
        }
    }

    private static final class Requires extends ArrayList<String> {

        private static final long serialVersionUID = -8583507670639301677L;

        public Requires(final Setting[] settings) {
            for (final Setting setting : settings) {
                add(setting.key());
            }
        }
    }
}

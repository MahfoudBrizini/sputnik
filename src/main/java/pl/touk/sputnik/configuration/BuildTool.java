package pl.touk.sputnik.configuration;

public enum BuildTool {
    //meow
    MAVEN("maven"), GRADLE("gradle");

    private final String name;

    BuildTool(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

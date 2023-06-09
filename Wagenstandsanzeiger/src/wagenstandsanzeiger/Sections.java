package wagenstandsanzeiger;

import java.util.List;

public class Sections {

    private String station;
    private String train;
    private String waggon;
    private List<String> sections;

    public Sections(String station, String train, String waggon, List<String> sections) {
        this.station = station;
        this.train = train;
        this.waggon = waggon;
        this.sections = sections;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }

    public String getWaggon() {
        return waggon;
    }

    public void setWaggon(String waggon) {
        this.waggon = waggon;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }
}

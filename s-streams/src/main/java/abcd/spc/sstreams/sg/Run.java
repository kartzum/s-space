package abcd.spc.sstreams.sg;

public class Run {
    private String key;
    private RunType type;
    private String responseKey;
    private String body;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RunType getType() {
        return type;
    }

    public void setType(RunType type) {
        this.type = type;
    }

    public String getResponseKey() {
        return responseKey;
    }

    public void setResponseKey(String responseKey) {
        this.responseKey = responseKey;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Run() {
    }

    public Run(String key, RunType type, String responseKey, String body) {
        this.key = key;
        this.type = type;
        this.responseKey = responseKey;
        this.body = body;
    }
}

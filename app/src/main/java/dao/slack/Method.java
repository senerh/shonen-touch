package dao.slack;

public class Method {

    private static final String SLACK_API_URL = "https://slack.com/api/";
    private static final String SLACK_API_TOKEN = "xoxp-124849748295-124849748391-128302964022-5dabc44ea18eec748854a8e05094cc40";

    private String url;

    public Method(String method) {
        url = SLACK_API_URL + method + "?token=" + SLACK_API_TOKEN;
    }

    public Method addArgument(String name, String value) {
        url += "&" + name + "=" + value;
        return this;
    }

    public String getUrl() {
        return url;
    }
}

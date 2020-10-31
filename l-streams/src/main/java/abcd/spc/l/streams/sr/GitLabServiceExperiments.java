package abcd.spc.l.streams.sr;

import java.util.List;

public class GitLabServiceExperiments {
    public static void main(String[] args) {
        // getVariablesByProjectId();
        createVariable();
    }

    static void getVariablesByProjectId() {
        GitLabService service = new GitLabService(getGitLabToken());
        List<GitLabService.Variable> variables = service.getVariablesByProjectId("20521468");
        variables.forEach((v) -> {
            System.out.println(v.key + ": " + v.value);
        });
    }

    static String getGitLabToken() {
        return System.getenv().get("GITLAB_TOKEN");
    }

    static void createVariable() {
        GitLabService service = new GitLabService(getGitLabToken());
        service.createVariable("20521468", "M_KEY", "M_VALUE");
    }
}

package nl.uu.cs.ape.functional;

public class UseCaseInfo {

    public final String name;
    public final String commit;
    public final String config_path;
    public final UseCaseMutation[] mutations;

    public UseCaseInfo(String name, String commit, String config_path, UseCaseMutation[] mutations){
        this.name = name;
        this.commit = commit;
        this.config_path = config_path;
        this.mutations = mutations;
    }
}

package cymru.mab152.mcp.exception;

public class TokenExpiredException extends Throwable {

    public TokenExpiredException() {
        super("15 minutes has passed, you cannot exit!" +
                "\nFind the nearest parking assistant.");
    }
}

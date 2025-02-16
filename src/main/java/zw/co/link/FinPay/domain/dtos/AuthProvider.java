package zw.co.link.FinPay.domain.dtos;

public enum AuthProvider {
    LOCAL,       // For users registered locally in the app
    GOOGLE,      // For users authenticated via Google
    FACEBOOK,    // For users authenticated via Facebook
    GITHUB       // For users authenticated via GitHub
}

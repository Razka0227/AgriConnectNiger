package ne.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ussd")
@RequiredArgsConstructor
public class UssdController {

    private static final Map<String, String> PRICE_SNIPPET =
            Map.of("mil", "Mil (sac 100kg) : 28 000 FCFA a Maradi", "oignon", "Oignon : 12 500 FCFA/sac a Tahoua");

    @PostMapping
    public Map<String, String> ussd(@RequestBody Map<String, String> payload) {
        String text = payload.getOrDefault("text", "");
        String sessionId = payload.getOrDefault("sessionId", "sess");

        if (text == null || text.isBlank()) {
            return response(sessionId, "CON Bienvenue sur AgriConnect Niger\n"
                    + "1. Prix du marche\n"
                    + "2. Mes commandes\n"
                    + "3. Meteo de ma region");
        }
        switch (text) {
            case "1" -> {
                return response(sessionId, "CON Choisissez un produit\n1. Mil\n2. Oignon\n3. Niebe\n0. Retour");
            }
            case "1*1" -> {
                return response(sessionId, "END " + PRICE_SNIPPET.get("mil"));
            }
            case "1*2" -> {
                return response(sessionId, "END " + PRICE_SNIPPET.get("oignon"));
            }
            case "1*3" -> {
                return response(sessionId, "END Niebe : 35 000 FCFA/sac a Zinder");
            }
            case "2" -> {
                return response(sessionId, "END Vous avez 2 commandes en cours. Derniere commande #142 confirmee.");
            }
            case "3" -> {
                return response(sessionId, "END Meteo Maradi : ensoleille 35°C, pluie attendue jeudi.");
            }
            default -> {
                return response(sessionId, "CON Choix invalide. Repondez 0 pour le menu principal.");
            }
        }
    }

    private Map<String, String> response(String sessionId, String text) {
        return Map.of("sessionId", sessionId, "text", text);
    }
}

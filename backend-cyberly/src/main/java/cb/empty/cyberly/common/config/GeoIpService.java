package cb.empty.cyberly.common.config;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoIpService {

    private static final String API_URL = "http://ip-api.com/json/%s?fields=country";
    private final RestTemplate restTemplate = new RestTemplate();

    public String getCountry(String ip) {
        if (ip == null || ip.isBlank() || isLocalIp(ip)) {
            return "Local";
        }
        try {
            String url = String.format(API_URL, ip);
            GeoResponse response = restTemplate.getForObject(url, GeoResponse.class);
            if (response != null && response.country() != null) {
                return response.country();
            }
        } catch (Exception ignored) {
        }
        return "Unknown";
    }

    private boolean isLocalIp(String ip) {
        return ip.equals("127.0.0.1")
                || ip.equals("0:0:0:0:0:0:0:1")
                || ip.startsWith("192.168.")
                || ip.startsWith("10.")
                || ip.startsWith("172.");
    }

    private record GeoResponse(String country) {}
}

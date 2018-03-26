package io.github.jokoframework.utils.http;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtils implements Serializable{
    
    
    private static final Logger LOG = LoggerFactory.getLogger(IpUtils.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7733137541374460905L;
	public static List<String> redesPrivadas = Arrays.asList("10.", "192.168.",
			"172.16.", "172.31.", "127.0.0.");

	public boolean isPrivateNework(String ip) {
		boolean ret = false;
		if (ip != null) {
			for (String red : redesPrivadas) {
				if (ip.startsWith(red)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	public Optional<String> getIp() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return Optional.of(ip.getHostAddress());
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}

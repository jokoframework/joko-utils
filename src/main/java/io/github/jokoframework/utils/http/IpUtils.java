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

	/**
	 * Verifica si el ip proveído esta en uno de los rangos de Ip asignados a redes privadas o si esta en el rango
	 * asignado al localhost
	 *
	 * @param ip Ip a verificar si es una dirección de red privada o del localhost
	 * @return True si el Ip proveído pertenece a una red privada o al localhost, sino false
	 */
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

	/**
	 * Devuelve un objeto contenedor del tipo "Optional", si el Ip del host se pudo obtener se retorna el objeto
	 * conteniendo el ip, de lo contrario se retorna el objeto conteniendo null
	 *
	 * @return Un objeto contenedor "Optional" cuyo valor contenido puede ser el Ip del host o null
	 */
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

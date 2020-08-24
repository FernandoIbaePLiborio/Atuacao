package com.gps.selenium.service.request;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.gps.selenium.enums.TipoAtuacao;
import com.gps.selenium.model.Defeito;
import com.gps.selenium.model.Integrante;

public class Request {
	
//	public static final Logger LOGGER = LogManager.getLogger(Request.class);
	
	public static Defeito excutePost(String targetURL, String urlParameters, Collection<Integrante> integrantes, Defeito defeito) {
		URL url;
		HttpURLConnection connection = null;
		try {
			
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			List<Integrante> atuantes = new ArrayList<>();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
				response.append('\r');
			}
			String res = response.toString();
			for (Integrante integrante : integrantes) {
				
				if (res.toUpperCase().contains(integrante.getName().toUpperCase())) {
					atuantes.add(integrante);
					System.out.println(integrante.getName());
				}
			}
			if (CollectionUtils.isNotEmpty(atuantes)) {
				
				if (!atuantes.stream().filter(i -> TipoAtuacao.DESENVOLVIMENTO.getValor().equals(i.getType())).collect(Collectors.toList()).isEmpty()
						&& !atuantes.stream().filter(i -> TipoAtuacao.FUNCIONAL.getValor().equals(i.getType())).collect(Collectors.toList()).isEmpty()) {
					System.out.println(TipoAtuacao.AMBOS.getValor());
					defeito.setAtuacao(TipoAtuacao.AMBOS.getValor());
				} else if (atuantes.stream().anyMatch(i -> TipoAtuacao.DESENVOLVIMENTO.getValor().equals(i.getType()))) {
					System.out.println(TipoAtuacao.DESENVOLVIMENTO.getValor());
					defeito.setAtuacao(TipoAtuacao.DESENVOLVIMENTO.getValor());
				} else if (atuantes.stream().anyMatch(i -> TipoAtuacao.FUNCIONAL.getValor().equals(i.getType()))) {
					System.out.println(TipoAtuacao.FUNCIONAL.getValor());
					defeito.setAtuacao(TipoAtuacao.FUNCIONAL.getValor());
				}
				List<String> resp = new ArrayList<String>();
				for (Integrante integrante : atuantes) {
					
					resp.add(integrante.getName());
				}
				defeito.setResponsavel(resp.toString().replace("[", "").replace("]", ""));
			} else {
				defeito.setAtuacao(TipoAtuacao.OUTRAS_ESQUIPES.getValor());
				defeito.setResponsavel("-");
			}
			rd.toString();
			rd.close();
			return defeito;

		} catch (Exception e) {
			System.out.println(targetURL.concat(urlParameters));
			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}

package com.gps.selenium.service;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import com.gps.selenium.model.Defeito;
import com.gps.selenium.model.Integrante;

import br.com.gvt.jeemodelinfra.exception.DAOException;

@Local
public interface DefeitoService {
	
	public String JNDI_NAME = "ejb/DefeitoServiceImpl";
	
	public void salvar(Defeito defeito);

	public List<Defeito> importarPlanilha(final InputStream inputStream);

	public Collection<Defeito> buscarAtuacao(Collection<Integrante> integrantes) throws DAOException;

	public Collection<Integrante> importarIntegrantes(InputStream inputstream);

}

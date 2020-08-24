package com.gps.selenium.business.dao;

import java.util.Collection;

import com.gps.selenium.model.Defeito;

import br.com.gvt.jeemodelinfra.dao.GenericDAO;
import br.com.gvt.jeemodelinfra.exception.DAOException;

public interface DefeitoDao extends GenericDAO<Defeito, Long> {

	public Collection<Defeito> buscarAtuacao() throws DAOException;

}

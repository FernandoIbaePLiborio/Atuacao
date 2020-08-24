package com.gps.selenium.business.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gps.selenium.business.dao.DefeitoDao;
import com.gps.selenium.model.Defeito;

import br.com.gvt.jeemodelinfra.dao.AbstractGenericDAO;
import br.com.gvt.jeemodelinfra.exception.DAOException;

@Stateless
public class DefeitoDaoImpl extends AbstractGenericDAO<Defeito, Long> implements DefeitoDao {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public EntityManager getEntityManager() {
		
		return this.em;
	}

	@Override
	public Collection<Defeito> buscarAtuacao() throws DAOException {
		try {
			
			final StringBuilder jpql = new StringBuilder();
			final Collection<String> codigos = new ArrayList<String>();

//			String list = Arrays.toString(codigos.toArray()).replace("[", "").replace("]", "").replace(",", "','").trim();
			jpql.append(" SELECT BUGID AS DEFEITO, PROJECTS AS DOMINIO, PROJETO, DIRETORIA, PACOTE, BUG_STATUS AS STATUS, ROOT_CAUSE, RESPONSIBLE_GROUP AS RESPONSÁVEL, ");
			jpql.append(" environment_type AS ESTEIRA, SISTEMA_ORIGEM, SISTEMA_CORRECAO, VENDOR AS EQUIPE, RDM AS CRQ, REOPENED, FIXING, DESENVOLVIMENTO, FUNCIONAL, ");
			jpql.append(" data_criacao, data_fechamento, CONT_TESTE, cont_desenvolvimento, cont_funcional, reopen, responsavel_correcao ");
			jpql.append(" FROM DASHBOARD_IIKPIS_OWNER.QA_DADOS_DASHBOARD_BUGS ");
			jpql.append(" WHERE DATA_FECHAMENTO > TO_DATE('01/05/2020', 'dd/mm/yyyy') ");
			jpql.append(" AND (SISTEMA_ORIGEM LIKE '%GPS%' OR SISTEMA_CORRECAO LIKE '%GPS%' OR SISTEMA_ORIGEM LIKE '%FIXA%' OR SISTEMA_CORRECAO LIKE '%FIXA%' OR VENDOR LIKE '%TELEFONICA%') " );
			jpql.append(" AND BUG_STATUS IN('Closed', 'Not Fixed', 'Rejected') ORDER BY BUGID DESC ");
			final Query query = this.em.createNativeQuery(jpql.toString());

			final List<Object[]> resultado = query.getResultList();
			final Collection<Defeito> defeitos = new ArrayList<Defeito>();

			for (int i = 0; i <= (resultado.size() - 1); i++) {
				final Object[] object = resultado.get(i);
				final Defeito defeito = new Defeito();
				defeito.setBug((String) object[0]);
				defeito.setDomain((String) object[1]);
				defeito.setProjeto((String) object[2]);
				defeitos.add(defeito);
			}
			return defeitos;
		} catch (final Exception e) {
			throw new DAOException(e);
		}
	}

	@Override
	protected Class<Defeito> getEntityClass() {
		
		return Defeito.class;
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arcler.tutorial.sakila;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alici
 */
@Stateless
public class FilmCategoryFacade extends AbstractFacade<FilmCategory> {

    @PersistenceContext(unitName = "SakilawebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FilmCategoryFacade() {
        super(FilmCategory.class);
    }
    
}

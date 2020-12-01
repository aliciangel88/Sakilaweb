package com.arcler.tutorial.sakila;

import com.arcler.tutorial.sakila.util.JsfUtil;
import com.arcler.tutorial.sakila.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("filmCategoryController")
@SessionScoped
public class FilmCategoryController implements Serializable {

    @EJB
    private com.arcler.tutorial.sakila.FilmCategoryFacade ejbFacade;
    private List<FilmCategory> items = null;
    private FilmCategory selected;

    public FilmCategoryController() {
    }

    public FilmCategory getSelected() {
        return selected;
    }

    public void setSelected(FilmCategory selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getFilmCategoryPK().setCategoryId(selected.getCategory().getCategoryId());
        selected.getFilmCategoryPK().setFilmId(selected.getFilm().getFilmId());
    }

    protected void initializeEmbeddableKey() {
        selected.setFilmCategoryPK(new com.arcler.tutorial.sakila.FilmCategoryPK());
    }

    private FilmCategoryFacade getFacade() {
        return ejbFacade;
    }

    public FilmCategory prepareCreate() {
        selected = new FilmCategory();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("FilmCategoryCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("FilmCategoryUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("FilmCategoryDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<FilmCategory> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public FilmCategory getFilmCategory(com.arcler.tutorial.sakila.FilmCategoryPK id) {
        return getFacade().find(id);
    }

    public List<FilmCategory> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<FilmCategory> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = FilmCategory.class)
    public static class FilmCategoryControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FilmCategoryController controller = (FilmCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "filmCategoryController");
            return controller.getFilmCategory(getKey(value));
        }

        com.arcler.tutorial.sakila.FilmCategoryPK getKey(String value) {
            com.arcler.tutorial.sakila.FilmCategoryPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.arcler.tutorial.sakila.FilmCategoryPK();
            key.setFilmId(Short.parseShort(values[0]));
            key.setCategoryId(Short.parseShort(values[1]));
            return key;
        }

        String getStringKey(com.arcler.tutorial.sakila.FilmCategoryPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getFilmId());
            sb.append(SEPARATOR);
            sb.append(value.getCategoryId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof FilmCategory) {
                FilmCategory o = (FilmCategory) object;
                return getStringKey(o.getFilmCategoryPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), FilmCategory.class.getName()});
                return null;
            }
        }

    }

}

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

@Named("filmActorController")
@SessionScoped
public class FilmActorController implements Serializable {

    @EJB
    private com.arcler.tutorial.sakila.FilmActorFacade ejbFacade;
    private List<FilmActor> items = null;
    private FilmActor selected;

    public FilmActorController() {
    }

    public FilmActor getSelected() {
        return selected;
    }

    public void setSelected(FilmActor selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getFilmActorPK().setFilmId(selected.getFilm().getFilmId());
        selected.getFilmActorPK().setActorId(selected.getActor().getActorId());
    }

    protected void initializeEmbeddableKey() {
        selected.setFilmActorPK(new com.arcler.tutorial.sakila.FilmActorPK());
    }

    private FilmActorFacade getFacade() {
        return ejbFacade;
    }

    public FilmActor prepareCreate() {
        selected = new FilmActor();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("FilmActorCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("FilmActorUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("FilmActorDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<FilmActor> getItems() {
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

    public FilmActor getFilmActor(com.arcler.tutorial.sakila.FilmActorPK id) {
        return getFacade().find(id);
    }

    public List<FilmActor> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<FilmActor> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = FilmActor.class)
    public static class FilmActorControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FilmActorController controller = (FilmActorController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "filmActorController");
            return controller.getFilmActor(getKey(value));
        }

        com.arcler.tutorial.sakila.FilmActorPK getKey(String value) {
            com.arcler.tutorial.sakila.FilmActorPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.arcler.tutorial.sakila.FilmActorPK();
            key.setActorId(Short.parseShort(values[0]));
            key.setFilmId(Short.parseShort(values[1]));
            return key;
        }

        String getStringKey(com.arcler.tutorial.sakila.FilmActorPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getActorId());
            sb.append(SEPARATOR);
            sb.append(value.getFilmId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof FilmActor) {
                FilmActor o = (FilmActor) object;
                return getStringKey(o.getFilmActorPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), FilmActor.class.getName()});
                return null;
            }
        }

    }

}

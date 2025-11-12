package com.library.converters;

import com.library.dao.BookDao;
import com.library.entities.Book;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("bookConverter")
public class BookConverter implements Converter {

    private BookDao bookDao;

    public BookConverter() {
        bookDao = new BookDao();
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        System.out.println("BookConverter.getAsObject - value: " + value);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            Book book = bookDao.findById(id);
            System.out.println("BookConverter.getAsObject - found book: " + (book != null ? book.getTitle() : "null"));
            return book;
        } catch (NumberFormatException e) {
            System.err.println("BookConverter.getAsObject - NumberFormatException for value: " + value + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        System.out.println("BookConverter.getAsString - value: " + value);
        if (value == null) {
            return "";
        }
        if (value instanceof Book) {
            String id = ((Book) value).getBookId().toString();
            System.out.println("BookConverter.getAsString - returning ID: " + id);
            return id;
        }
        System.err.println("BookConverter.getAsString - value is not a Book instance: " + value.getClass().getName());
        return "";
    }
}

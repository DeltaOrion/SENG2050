package com.example.assignment_1.presentation;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PagePresenter {

    /**
     * Presents something as HTML. This may or may not create an entire page and may only add a specific html element.
     * This should be documented on the present method itself.
     *
     * @param response The response to write to
     * @throws IOException If an error occurs while writing.
     */
    public void present(HttpServletResponse response) throws IOException;
}

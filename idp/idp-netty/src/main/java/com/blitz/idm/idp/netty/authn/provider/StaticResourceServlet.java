package com.blitz.idm.idp.netty.authn.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class StaticResourceServlet extends HttpServlet {

    /**
     * Class logger.
     */
    private final Logger log = LoggerFactory.getLogger(StaticResourceServlet.class);

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/*        String uri = Utils.sanitizeUri(request.getRequestURI());
        final String path = ("D:/src_esia/netty_idp/idp-com.blitz.idm.idp.netty/src/main/webapp" + uri);
        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            //sendError(ctx, NOT_FOUND);
            return;
        }
        if (!file.isFile()) {
            //sendError(ctx, FORBIDDEN);
            return;
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnfe) {
            //sendError(ctx, NOT_FOUND);
            return;
        }

        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        setContentLength(response, fileLength);

        Channel ch = e.getChannel();

        // Write the initial line and the header.
        ch.write(response);
        PrintWriter out = response.getWriter();*/
    }



}
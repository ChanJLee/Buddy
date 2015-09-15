package filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by chan on 15-9-12.
 */
public class GzipFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        filterChain.doFilter(new GzipRequestWrapper((HttpServletRequest) servletRequest),
                servletResponse);
    }

    @Override
    public void destroy() {}

    private static final class GzipRequestWrapper extends HttpServletRequestWrapper {
        private HttpServletRequest request;

        public GzipRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ServletInputStream stream = request.getInputStream();
            String contentEncoding = request.getHeader("Content-Encoding");

            // 如果对内容进行了压缩，则解压
            if (null != contentEncoding && contentEncoding.indexOf("gzip") != -1) {
                try {
                    final GZIPInputStream gzipInputStream = new GZIPInputStream(
                            stream);

                    ServletInputStream gzipStream = new ServletInputStream() {
                        @Override
                        public boolean isFinished() { return false; }
                        @Override
                        public boolean isReady() { return false; }
                        @Override
                        public void setReadListener(ReadListener readListener) {}

                        @Override
                        public int read() throws IOException {
                            return gzipInputStream.read();
                        }
                    };

                    return gzipStream;
                } catch (Exception e) {}
            }

            return stream;
        }
    }
}

package com.leonardofischer.util

class ClasspathUtils {

    /**
     * Carrega o resource indicado em um string a partir do classpath
     * da aplicação.
     */
    protected static String getResourceAsString(String resourceName) {
        return convertStreamToString( ClasspathUtils.class.getClassLoader().getResourceAsStream(resourceName) )
    }

    /**
     * Carrega o conteúdo do InputStream em um string. Código
     * copiado de http://stackoverflow.com/a/3891433
     */
    protected static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
    
}

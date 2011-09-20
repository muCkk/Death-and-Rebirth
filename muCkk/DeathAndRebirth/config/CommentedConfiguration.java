package muCkk.DeathAndRebirth.config;

import org.bukkit.util.config.Configuration;

import java.io.*;
import java.util.HashMap;

public class CommentedConfiguration extends Configuration {

    private HashMap<String, String> comments;
    private File file;

    public CommentedConfiguration(File file) {
        super(file);
        comments = new HashMap<String, String>();
        this.file = file;
    }

    @Override
    public boolean save() {
        boolean saved = super.save();
    
        if (!comments.isEmpty() && saved) {
            String[] yamlContents =
                    convertFileToString(file).split("[" + System.getProperty("line.separator") + "]");
            String configWithComments = "";
            
            for (String node : yamlContents) {
            	if(node == null) {
            		configWithComments +=System.getProperty("line.separator");
            		continue;
            	}
				if(comments.containsKey(node.split(":")[0])) {
					configWithComments += comments.get(node.split(":")[0]) + System.getProperty("line.separator");
				}
				configWithComments += node + System.getProperty("line.separator");
            }  
            
            try {
            	stringToFile(configWithComments, file);
            } catch (IOException e) {
                saved = false;
            }
        }

        return saved;
    }

    /**
     * Adds a comment just before the specified path.  The comment can be multiple lines.  An empty string will indicate a blank line.
     * @param path Configuration path to add comment.
     * @param commentLines Comments to add.  One String per line.
     */
    public void addComment(String path, String...commentLines) {
        StringBuilder commentstring = new StringBuilder();
        String leadingSpaces = "";
        for (int n = 0; n < path.length(); n++) {
            if (path.charAt(n) == '.') {
                leadingSpaces += "    ";
            }
        }
        for (String line : commentLines) {
            if (!line.isEmpty()) {
            	line = leadingSpaces + line;
            } else {
                line = " ";
            }
            if (commentstring.length() > 0) {
                commentstring.append("\r\n");
            }
            commentstring.append(line);
        }
        comments.put(path, commentstring.toString());
    }

    /**
     * Pass a file and it will return it's contents as a string.
     * @param file File to read.
     * @return Contents of file.  String will be empty in case of any errors.
     */
    private String convertFileToString(File file) {
        if (file != null && file.exists() && file.canRead() && !file.isDirectory()) {
            Writer writer = new StringWriter();
            InputStream is = null;

            char[] buffer = new char[1024];
            try {
                is = new FileInputStream(file);
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (IOException e) {
                System.out.println("Exception ");
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignore) {}
                }
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    /**
     * Writes the contents of a string to a file.
     * @param source String to write.
     * @param file File to write to.
     * @return True on success.
     * @throws IOException
     */
    private boolean stringToFile(String source, File file) throws IOException {
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");

            source.replaceAll("\n", System.getProperty("line.separator"));

            out.write(source);
            out.close();
            return true;
        } catch (IOException e) {
            System.out.println("Exception ");
            return false;
        }
    }
}
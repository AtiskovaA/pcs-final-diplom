import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> textMap = new HashMap<>();
    private Set<String> stopWord = new HashSet<>();
    private List<PageEntry> pageList = new ArrayList<>();

    //(для этого можно использовать мапу, где ключом будет слово, а значением - искомый список).

    public List<Object> BooleanSearchEngine(File pdfsDir) throws IOException {

        if (pdfsDir.isDirectory()) {
            for (File files : pdfsDir.listFiles()) {
                var doc = new PdfDocument(new PdfReader(files));

                for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    String filePdf = files.getName();

                    var words = text.split("\\P{IsAlphabetic}+");

                    Map<String, Integer> freqs = new HashMap<>();
                    for (var word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }

                    for (String quantity : freqs.keySet()) {
                        PageEntry pageEntry = new PageEntry(files.getName(), i, freqs.get(quantity));
                        if (!textMap.containsKey(quantity)) {
                            List<PageEntry> pageNew = new ArrayList<>();
                            pageNew.add(pageEntry);
                            textMap.put(quantity, pageNew);
                        } else {
                            textMap.get(quantity).add(pageEntry);
                        }
                        pageList = new ArrayList<>(textMap.get(quantity.toLowerCase()));
                        pageList.sort(Collections.reverseOrder());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<PageEntry> search(String appeal) {
        if ((appeal == null) || (appeal.isEmpty())) {
            return Collections.emptyList();
        }
        return pageList;
    }
}

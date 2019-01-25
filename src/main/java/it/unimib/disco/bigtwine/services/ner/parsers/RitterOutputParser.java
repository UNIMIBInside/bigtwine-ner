package it.unimib.disco.bigtwine.services.ner.parsers;

import it.unimib.disco.bigtwine.commons.models.RecognizedTweet.Entity;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

final public class RitterOutputParser implements OutputParser {

    private Reader reader;
    private BufferedReader buffer;
    private RecognizedTweet nextTweet;

    public RitterOutputParser() {
    }

    public RitterOutputParser(Reader reader) {
        this.setReader(reader);
    }

    public RitterOutputParser(File file) throws FileNotFoundException {
        this(new FileReader(file));
    }

    public RitterOutputParser(String string) {
        this(new StringReader(string));
    }

    private enum Kind {
        id, text, entity
    }

    private class Line {
        private Kind kind;
        private String content;

        private Line(Kind kind, String content) {
            this.kind = kind;
            this.content = content;
        }

        private Line(Kind kind) {
            this(kind, null);
        }
    }

    public class MalformedText extends Exception {

    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
        this.buffer = new BufferedReader(reader);
    }

    private boolean isValidTweet(RecognizedTweet tweet) {
        boolean isValid = true;
        isValid = isValid && tweet.getId() != null && !tweet.getId().isEmpty();
        isValid = isValid && tweet.getText() != null && !tweet.getText().isEmpty();
        return isValid;
    }

    private Line parseLine(String line) {
        if (line == null) {
            return null;
        }

        line = line.trim();

        if (line.startsWith("[#ID#]")) {
            return new Line(Kind.id, line.substring(7));
        }else if (line.startsWith("[#TWEET#]")) {
            return new Line(Kind.text, line.substring(10));
        }else if (line.startsWith("[#ETS#]")) {
            return new Line(Kind.entity, line.substring(8));
        }else {
            return null;
        }
    }

    private Entity parseEntity(String line) {
        Entity entity = new Entity();
        String[] parts = line.split("\\t+");
        if (parts.length >= 3) {
            entity.setValue(parts[0].trim());
            entity.setLabel(parts[1].trim());
            entity.setProbability(Float.parseFloat(parts[2].trim()));
        }

        return entity;
    }

    private RecognizedTweet parse(boolean skipInvalids) throws IOException, MalformedText {
        if (this.buffer == null) throw new AssertionError("A reader was not set");
        RecognizedTweet tweet = new RecognizedTweet();
        List<Entity> tweetEntities = new ArrayList<>();

        String l;
        while ((l = buffer.readLine()) != null) {
            if (l.trim().isEmpty()) {
                break;
            }

            Line line = this.parseLine(l);

            if (line == null) {
                if (skipInvalids) {
                    continue; // Invalid line, skip
                }else {
                    throw new MalformedText();
                }
            }

            switch (line.kind) {
                case id:
                    tweet.setId(line.content);
                    break;
                case text:
                    tweet.setText(line.content);
                    break;
                case entity:
                    Entity e = this.parseEntity(line.content);
                    if (e != null) {
                        tweetEntities.add(e);
                    }
                    break;
            }
        }

        tweet.setEntities(tweetEntities.toArray(new Entity[0]));

        if (this.isValidTweet(tweet)) {
            return tweet;
        }else {
            if (l != null) {
                return this.parse();
            }else {
                // File terminato
                return null;
            }
        }
    }

    private RecognizedTweet parse() throws IOException, MalformedText {
        return this.parse(true);
    }


    @Override
    public boolean hasNext() {
        if (this.nextTweet == null) {
            try {
                this.nextTweet = this.parse();
            } catch (IOException | MalformedText e) {
                this.nextTweet = null;
            }
        }

        return this.nextTweet != null;
    }

    @Override
    public RecognizedTweet next() {
        if (this.hasNext()) {
            RecognizedTweet tweet = this.nextTweet;
            this.nextTweet = null;
            return tweet;
        }

        throw new NoSuchElementException();
    }

    @Override
    public RecognizedTweet[] items() {
        List<RecognizedTweet> tweets = new ArrayList<>();
        while (this.hasNext()) {
            tweets.add(this.nextTweet);
            this.nextTweet = null;
        }

        return tweets.toArray(new RecognizedTweet[0]);
    }
}

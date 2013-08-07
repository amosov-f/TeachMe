package com.kk.teachme.service;

import com.kk.teachme.db.*;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCTLoader {

    ConfigDepot configDepot;

    TagDepot tagDepot;

    FileDepot fileDepot;

    ProblemDepot problemDepot;

    SolutionDepot solutionDepot;

    public void fill() {
        fill(5);
    }

    public void fill(int cards) {

        if (cards > 40 || cards < 1) {
            cards = 1;
        }

        int tct;
        try {
            tct = configDepot.getValue("tct");
        } catch(IndexOutOfBoundsException e) {
            configDepot.addVariable("tct", 0);
            tct = 0;
        }

        tagDepot.createIfNotExist("пдд");

        for (int i = tct; i < cards; i++) {
            String cardURLString = "http://www.pddrussia.com/static/ab/bilet/b" + (i+1) + ".json";
            get(cardURLString);
        }

        configDepot.setValue("tct", cards > tct ? cards : tct);

    }

    private void get(String cardURLString) {

        try {

            //Forming a JSONArray of questions from a given card URL.
            URL cardURL = new URL(cardURLString);
            Scanner cardScanner = new Scanner(cardURL.openStream(),"UTF-8");
            JSONArray card = new JSONArray(cardScanner.useDelimiter("\\A").next());

            //Card ID is stored in each question, but it is the same for all
            //questions in the card, so you only have to pull it out once.
            int cardId;
            try {
                cardId = card.getJSONObject(0).getInt("biletNumber");
            } catch (JSONException e) {
                return;
            }

            //Now each question will form a ProblemSolution instance.
            for (int i = 0; ; i++) {

                try {

                    JSONObject question = card.getJSONObject(i);

                    //Forming a 'name' for a Problem constructor.
                    int questionId = question.getInt("questNumber");
                    String name = "Экзамен ПДД. Билет " + cardId + ", вопрос " + questionId;

                    //Forming a 'statement' for a Problem constructor.
                    String statement = question.getString("quest");

                    //Adding answer options to the 'statement' in a loop.
                    JSONArray options = question.getJSONArray("v");
                    for (int j = 0; ; j++) {

                        try {

                            String newOption = options.getString(j);
                            if (newOption.equals("null")) {
                                break;
                            }
                            statement += "\n" + (j+1) + ") " + newOption;

                        } catch (JSONException e) {
                            break;
                        }

                    }

                    //Forming a 'solutionText' for a Solution constructor.
                    String solutionText = ((Integer)question.getInt("otvet")).toString();

                    //Forming an 'imageURL' for an OverProblem constructor.
                    List<String> figures = new ArrayList<String>();
                    try {
                        URL imageURL = new URL(question.getString("realUrl"));
                        BufferedImage image = ImageIO.read(imageURL);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", byteStream);
                        byteStream.flush();
                        byte[] byteArray = byteStream.toByteArray();
                        byteStream.close();
                        figures.add(fileDepot.addNewFile(byteArray));
                    } catch (JSONException e) {
                        //System.out.println("Couldn't get an image address");
                    } catch (MalformedURLException e) {
                        //System.out.println("Image URL is invalid");
                    } catch (IOException e) {
                        //System.out.println("Couldn't handle the stream");
                    }

                    if (figures.size() == 0) {
                        figures.add("");
                    }

                    ArrayList<Tag> TCT = new ArrayList<Tag>();
                    TCT.add(tagDepot.getByName("пдд"));

                    solutionDepot.addSolution
                            (problemDepot.addObject
                                    (new Problem(name, statement, figures, TCT)), solutionText, 1);

                } catch (JSONException e) {
                    break;
                }

            }

        } catch (MalformedURLException e) {
            //System.out.println("URL is invalid");
        } catch (IOException e) {
            //System.out.println("Couldn't open the stream");
        } catch (JSONException e) {
            //System.out.println("Couldn't form a JSON array");
        }

    }

    @Required
    public void setConfigDepot(ConfigDepot configDepot) {
        this.configDepot = configDepot;
    }

    @Required
    public void setTagDepot(TagDepot tagDepot) {
        this.tagDepot = tagDepot;
    }

    @Required
    public void setProblemDepot(ProblemDepot problemDepot) {
        this.problemDepot = problemDepot;
    }

    @Required
    public void setSolutionDepot(SolutionDepot solutionDepot) {
        this.solutionDepot = solutionDepot;
    }

    @Required
    public void setFileDepot(FileDepot fileDepot) {
        this.fileDepot = fileDepot;
    }

}

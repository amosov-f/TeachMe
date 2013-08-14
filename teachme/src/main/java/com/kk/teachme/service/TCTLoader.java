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

        Integer tct = configDepot.getValue("tct");
        if (tct == null) {
            configDepot.addVariable("tct", 0);
            tct = 0;
        }

        tagDepot.createIfNotExist("пдд");

        int nailed;
        for (nailed = tct; nailed < cards; nailed++) {
            String cardURLString = "http://www.pddrussia.com/static/ab/bilet/b" + (nailed+1) + ".json";
            if (!get(cardURLString)) {
                System.out.println("Failed to load card #" + nailed + 1);
                break;
            }
        }

        if (nailed > tct) {
            configDepot.setValue("tct", nailed);
            System.out.println("Cards from " + (tct+1) + " to " + nailed + " loaded");
        }

    }

    private boolean get(String cardURLString) {

        try {

            URL cardURL = new URL(cardURLString);
            Scanner cardScanner = new Scanner(cardURL.openStream(),"UTF-8");
            JSONArray card = new JSONArray(cardScanner.useDelimiter("\\A").next());

            int cardId;
            cardId = card.getJSONObject(0).getInt("biletNumber");

            for (int i = 0; i < 20; i++) {

                JSONObject question = card.getJSONObject(i);

                int questionId = question.getInt("questNumber");
                String name = "Экзамен ПДД. Билет " + cardId + ", вопрос " + questionId;

                String statement = question.getString("quest");

                JSONArray options = question.getJSONArray("v");
                for (int j = 0; j < 5; j++) {

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

                String solutionText = ((Integer)question.getInt("otvet")).toString();

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
                } catch (Exception e) {
                    figures.add("");
                }

                ArrayList<Tag> TCT = new ArrayList<Tag>();
                TCT.add(tagDepot.getByName("пдд"));

                solutionDepot.addSolution
                    (problemDepot.addObject
                        (new Problem(name, statement, figures, TCT)), solutionText, 1);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;

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

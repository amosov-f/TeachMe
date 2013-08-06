package com.kk.teachme.servlet;

import com.kk.teachme.db.FileDepot;
import com.kk.teachme.db.ProblemDepot;
import com.kk.teachme.db.SolutionDepot;
import com.kk.teachme.db.TagDepot;
import com.kk.teachme.model.Problem;
import com.kk.teachme.model.Tag;
import com.kk.teachme.support.JSONCreator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Controller
@RequestMapping("/")
public class TCTController {

    @Autowired
    TagDepot tagDepot;

    @Autowired
    FileDepot fileDepot;

    @Autowired
    ProblemDepot problemDepot;

    @Autowired
    SolutionDepot solutionDepot;

    @RequestMapping(value = "/add_tct", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String fill(
            @RequestParam(required = false) Integer cards
    ) throws Exception {

        tagDepot.createIfNotExist("пдд");

        if (cards == null || cards > 40 || cards < 1) {
            cards = 1;
        }

        for (int i = 0; i < cards; i++) {
            String cardURLString = "http://www.pddrussia.com/static/ab/bilet/b" + (i+1) + ".json";
            get(cardURLString);
        }

        return JSONCreator.okJson().toString();

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

}

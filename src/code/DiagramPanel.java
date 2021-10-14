package code;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.InvalidPathException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.Iterator;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Interactive diagrams holder panel.
 */
public class DiagramPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Constants
    private static final int STRUT_WIDTH = 20;
    private static final double ZOOM_FACTOR = 1.4142; // sqrt(2)
    private static final double WHEEL_FACTOR = 2.8854; // 1/ln(sqrt(2))
    private static final double SCROLL_FACTOR = 10.0; // scrollbar pixels to
    // world pixel

    // UI components
    private final DiagramCanvas canvas;
    private final JScrollBar hsb;
    private final JScrollBar vsb;
    private final JToggleButton handButton;
    private final JToggleButton cursorButton;
    private final JLabel hintLabel;

    private final Point startPoint = new Point();
    private final Point currentPoint = new Point();
    private DiagramObject currentElement;

    private static DiagramObject rootDiagramObject;
    private double scale = 1.0;
    private boolean panningMode;
    private boolean mouseDown;
    public Class creatingBlock = null;

    public static boolean creatingLink = false;
    public static boolean creatingFromContextNode = false;

    public static final JToggleButton termButtonStart = new JToggleButton();
    public static final JToggleButton termButtonEnd = new JToggleButton();


    private static JToggleButton pressed = null;

    public static ContextFrame contextFrame = null;


    private final SelectionManager selection = new SelectionManager();
    private final DiagramObject lasso = new Lasso();
    private final DiagramObject strokeLine = new StrokeLine();

    Label labelExpression;

    public DiagramPanel() {
        setLayout(new BorderLayout(0, 0));
        canvas = new DiagramCanvas();

        canvas.addMouseWheelListener((MouseWheelEvent e) -> {
            canvasMouseWheel(e);
        });
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                canvasMouseDown(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                canvasMouseUp(e);
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                canvasMouseDragged(e);
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                canvasMouseMoved(e);
            }
        });

        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);

        vsb = new JScrollBar();
        vsb.getModel().addChangeListener((ChangeEvent changeEvent) -> {
            scrollBarChange();
        });

        hsb = new JScrollBar();
        hsb.getModel().addChangeListener((ChangeEvent changeEvent) -> {
            scrollBarChange();
        });
        add(vsb, BorderLayout.EAST);
        hsb.setOrientation(JScrollBar.HORIZONTAL);
        add(hsb, BorderLayout.SOUTH);

        JPanel northPanel = new JPanel();
        JPanel northLeftPanel = new JPanel();
        JPanel northRightPanel = new JPanel();

        add(northPanel, BorderLayout.NORTH);
        northPanel.setLayout(new BorderLayout());
        northPanel.add(northLeftPanel, BorderLayout.WEST);
        northPanel.add(northRightPanel, BorderLayout.EAST);

        //терминатор начала
        termButtonStart.addActionListener((ActionEvent e) -> {
            if (creatingBlock != null) {
                if (pressed == termButtonStart) {
                    pressed.setSelected(false);
                    pressed = null;
                    creatingBlock = null;
                } else {
                    pressed.setSelected(false);
                    creatingBlock = DiagramTerminatorStart.class;
                    pressed = termButtonStart;
                    pressed.setSelected(true);
                }
            } else {
                creatingBlock = DiagramTerminatorStart.class;
                pressed = termButtonStart;
                pressed.setSelected(true);
            }
        });

        termButtonStart.setIcon(new ImageIcon(AppStart.class.getResource("/resources/4.png")));



        System.out.println(termButtonStart.getMinimumSize().toString());

        //парараллелограмм
        JToggleButton parallelButton = new JToggleButton();
        parallelButton.addActionListener((ActionEvent e) -> {

            if(creatingBlock != null){
                if(pressed == parallelButton){
                    creatingBlock = null;
                    pressed.setSelected(false);
                    pressed = null;
                }
                else {
                    creatingBlock = DiagramParallelogram.class;
                    pressed.setSelected(false);
                    pressed = parallelButton;
                }
            }
            else {
                creatingBlock = DiagramParallelogram.class;
                pressed = parallelButton;
            }


        });

        parallelButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/2.png")));



        //прямоугольник
        JToggleButton rectButton = new JToggleButton();
        rectButton.addActionListener((ActionEvent e) -> {
            if(creatingBlock != null){
                if(pressed == rectButton){
                    creatingBlock = null;
                    pressed.setSelected(false);
                    pressed = null;
                }
                else {
                    creatingBlock = DiagramRectangle.class;
                    pressed.setSelected(false);
                    pressed = rectButton;
                }
            }
            else {
                creatingBlock = DiagramRectangle.class;
                pressed = rectButton;
            }
        });
        rectButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/1.png")));



        //ромб
        JToggleButton rhombusButton = new JToggleButton();
        rhombusButton.addActionListener((ActionEvent e) -> {

            if(creatingBlock != null){
                if(pressed == rhombusButton){
                    creatingBlock =null;
                    pressed.setSelected(false);
                    pressed = null;
                }
                else {
                    creatingBlock = DiagramRhombus.class;
                    pressed.setSelected(false);
                    pressed = rhombusButton;
                }
            }
            else {
                creatingBlock = DiagramRhombus.class;
                pressed = rhombusButton;
            }

        });

        rhombusButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/3.png")));

        //цикл
        JToggleButton diamondButton = new JToggleButton();
        diamondButton.addActionListener((ActionEvent e) -> {

            if(creatingBlock != null){
                if(pressed == diamondButton){
                    creatingBlock =null;
                    pressed.setSelected(false);
                    pressed = null;
                }
                else {
                    creatingBlock = DiagramDiamond.class;
                    pressed.setSelected(false);
                    pressed = diamondButton;
                }
            }
            else {
                creatingBlock = DiagramDiamond.class;
                pressed = diamondButton;
            }

        });
        diamondButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/6.png")));



        //препроцесс
        JToggleButton preprocessButton = new JToggleButton();
        preprocessButton.addActionListener((ActionEvent e) -> {

            if(creatingBlock != null){
                if(pressed == preprocessButton){
                    creatingBlock =null;
                    pressed.setSelected(false);
                    pressed = null;
                }
                else {
                    creatingBlock = DiagramPreprocess.class;
                    pressed.setSelected(false);
                    pressed = preprocessButton;
                }
            }
            else {
                creatingBlock = DiagramPreprocess.class;
                pressed = preprocessButton;
            }

        });
        preprocessButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/7.png")));



        // терминатор конца

        termButtonEnd.addActionListener((ActionEvent e) -> {
            if (creatingBlock != null) {
                if (pressed == termButtonEnd) {
                    creatingBlock = null;
                    pressed.setSelected(false);
                    pressed = null;
                } else {
                    pressed.setSelected(false);
                    creatingBlock = DiagramTerminatorEnd.class;
                    pressed = termButtonEnd;
                    pressed.setSelected(true);
                }
            } else {
                creatingBlock = DiagramTerminatorEnd.class;
                pressed = termButtonEnd;
                pressed.setSelected(true);
            }
        });

        termButtonEnd.setIcon(new ImageIcon(AppStart.class.getResource("/resources/5.png")));

        //панель выбора блоков
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        add(westPanel, BorderLayout.WEST);

        //кнопки выбора блоков
        JPanel jPanelExpression = new JPanel();
        labelExpression = new Label("Открытая схема: ");
        JButton backButton = new JButton("Назад");

        jPanelExpression.setLayout(new FlowLayout());
        jPanelExpression.add(labelExpression);
        jPanelExpression.add(backButton);

        JPanel blockPanel = new JPanel();
        blockPanel.setLayout(new BoxLayout(blockPanel, BoxLayout.Y_AXIS));
        blockPanel.setBorder(new EmptyBorder(0, 0, 0, 20));
        blockPanel.add(termButtonStart);
        blockPanel.add(Box.createRigidArea(new Dimension(20, 15)));
        blockPanel.add(parallelButton);
        blockPanel.add(Box.createRigidArea(new Dimension(20, 15)));
        blockPanel.add(rectButton);
        blockPanel.add(Box.createRigidArea(new Dimension(20, 15)));
        blockPanel.add(rhombusButton);
        blockPanel.add(Box.createRigidArea(new Dimension(20, 15)));
        blockPanel.add(diamondButton);
        blockPanel.add(Box.createRigidArea(new Dimension(20, 15)));
        blockPanel.add(preprocessButton);
        blockPanel.add(Box.createRigidArea(new Dimension(20, 15)));
        blockPanel.add(termButtonEnd);
        blockPanel.add(Box.createRigidArea(new Dimension(20, 15)));

        westPanel.add(blockPanel, BorderLayout.NORTH);

        JButton newFile = new JButton();
        newFile.addActionListener((ActionEvent e) -> {
            if(pressed != null){
                pressed.setSelected(false);
                pressed = null;
                creatingBlock = null;
            }
            if(rootDiagramObject.getFirstSubObj()!=null) {
                int option = JOptionPane.showConfirmDialog(null, "Желаете сохранить текущий файл? Все несохранённые данные будут утеряны.");
                switch (option) {
                    case (2):
                        return;
                    case (1):
                        break;
                    case (0):
                        saveFile();
                }
            }
            Scheme scheme = new Scheme(true);
            scheme.caption = "New scheme";
            AppStart.changeWindowTitle(scheme.caption);
            labelExpression.setText("Открытая схема: " + scheme.caption);
            setDiagramObject(scheme);
        });
        newFile.setIcon(new ImageIcon(AppStart.class.getResource("/resources/new.png")));
        northLeftPanel.add(newFile);

        JButton openFileButton = new JButton();

        openFileButton.addActionListener((ActionEvent e) -> {
            if(pressed != null) {
                pressed.setSelected(false);
                pressed = null;
                creatingBlock = null;
            }
            if(rootDiagramObject.getFirstSubObj()!=null) {
                int option = JOptionPane.showConfirmDialog(null, "Желаете сохранить текущий файл? Все несохранённые данные будут утеряны.");
                switch (option) {
                    case (2):
                        return;
                    case (1):
                        break;
                    case (0):
                        saveFile();
                }
            }
            JFileChooser jFileChooser = new JFileChooser("C:\\Users\\L\\Documents\\Schemes\\");
            jFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith("json")) return true;
                    return false;
                }
                @Override
                public String getDescription() {
                    return ".json";
                }
            });

            String dir, filename, filePath;
            int rVal = jFileChooser.showOpenDialog(northLeftPanel);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                filename = jFileChooser.getSelectedFile().getName();
                dir = jFileChooser.getCurrentDirectory().toString();
                filePath = dir + "\\" + filename;

                try {
                    openJSONfile(filePath);
                    getDiagramObject().caption = filename.split(".json")[0];
                    AppStart.changeWindowTitle(getDiagramObject().caption);
                    labelExpression.setText("Открытая схема: " + getDiagramObject().caption);

                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });

        openFileButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/open.png")));
        northLeftPanel.add(openFileButton);

        JButton saveFileButton = new JButton();
        saveFileButton.addActionListener((ActionEvent e) -> {
            if (rootDiagramObject.getFirstSubObj() != null) {
                saveFile();
            }
            else JOptionPane.showMessageDialog(new Frame(), "Нельзя сохранить пустой файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
        });

        saveFileButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/save.png")));
        northLeftPanel.add(saveFileButton);

        handButton = new JToggleButton();
        handButton.addActionListener((ActionEvent e) -> {
            setPanningMode(true);
        });
        handButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/hand.png")));
        northLeftPanel.add(handButton);

        cursorButton = new JToggleButton();
        cursorButton.addActionListener((ActionEvent e) -> {
            setPanningMode(false);
        });

        cursorButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/cursor.png")));
        northLeftPanel.add(cursorButton);

        setPanningMode(false);

        JButton plusButton = new JButton();
        plusButton.addActionListener((ActionEvent e) -> {
            zoomIn();
            canvas.paint(canvas.getGraphics());
        });

        plusButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/plus.png")));
        northLeftPanel.add(plusButton);

        JButton minusButton = new JButton();
        minusButton.addActionListener((ActionEvent e) -> {
            zoomOut();
            canvas.paint(canvas.getGraphics());
        });
        minusButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/minus.png")));
        northLeftPanel.add(minusButton);

        JButton one2oneButton = new JButton();
        one2oneButton.addActionListener((ActionEvent e) -> {
            setScale(1.0);
        });
        one2oneButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/1-1.png")));
        northLeftPanel.add(one2oneButton);

        JButton checkButton = new JButton();
        checkButton.addActionListener((ActionEvent e) ->
                checkDiagram(new Frame()));

        checkButton.setIcon(new ImageIcon(AppStart.class.getResource("/resources/check.png")));
        northLeftPanel.add(checkButton);

        Component horizontalStrut = Box.createHorizontalStrut(STRUT_WIDTH);
        northLeftPanel.add(horizontalStrut);

        hintLabel = new JLabel("");
        hintLabel.setBackground(Color.YELLOW);
        northLeftPanel.add(hintLabel);

        northRightPanel.add(jPanelExpression);

    }

    public void checkDiagram(Component parent){
        ArrayList<DiagramObject> objs = new ArrayList<>(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.values());

        //все блоки связаны
        DiagramTerminatorStart start = ((Scheme)DiagramPanel.getDiagramObject()).getStartTerm() ;
        if(start == null){
            JOptionPane.showMessageDialog(parent, "На схеме отсуствтует блок начала", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DiagramTerminatorEnd end = ((Scheme)DiagramPanel.getDiagramObject()).getEndTerm() ;
        if(end == null){
            JOptionPane.showMessageDialog(parent, "На схеме отсутствтует блок конца", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean errorFlag = false;
        String errorMessage = "В следующие блоки невозможно попасть из блока начала:";

        ArrayList<AbstractDiagramNode> objCopy = (ArrayList<AbstractDiagramNode>) objs.clone();
        ArrayList<AbstractDiagramNode> chainedNodes = start.getChainedBlocksDown();
        for (DiagramObject obj: (ArrayList<AbstractDiagramNode>) objs.clone()) {
            if (obj.getClass().equals(DiagramGeneralization.class)) objCopy.remove(obj);
        }
        for (DiagramObject obj: chainedNodes) {
            if (chainedNodes.contains(obj)) objCopy.remove(obj);
        }
        if(objCopy.size() != 0){
            errorFlag = true;
            for (DiagramObject node: objCopy) {
                errorMessage += "\n" + node.getName();
                ((AbstractDiagramNode)node).errorPaint();
                canvas.repaint();
            }
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(parent, errorMessage, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        errorMessage = "Из следующих блоков невозможность попасть к блоку конца:";
        for (DiagramObject obj: objs) {
            if(!obj.getClass().equals(DiagramGeneralization.class)){
                AbstractDiagramNode node = (AbstractDiagramNode)obj;
                if(!node.getClass().equals(DiagramTerminatorEnd.class) && node.get_lines_out().size() == 0){
                    errorFlag = true;
                    errorMessage += "\n" + node.getName();
                    ((AbstractDiagramNode)node).errorPaint();
                    canvas.repaint();
                }
            }
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(parent, errorMessage, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        errorMessage = "Блоки условий должны иметь ровно 2 выхода:";
        for (DiagramObject obj: objs) {
            if(!obj.getClass().equals(DiagramGeneralization.class)){
                AbstractDiagramNode node = (AbstractDiagramNode)obj;
                if(node.getClass().equals(DiagramRhombus.class) && node.get_lines_out().size() < 2){
                    errorFlag = true;
                    errorMessage += "\n" + node.getName();
                    ((AbstractDiagramNode)node).errorPaint();
                    canvas.repaint();
                }
            }
        }
        if(errorFlag){
            JOptionPane.showMessageDialog(parent, errorMessage, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SchemeCompiler schemeCompiler = new SchemeCompiler();
        try{
            schemeCompiler.compile(start.getChainedBlocksDown());
        }
        catch (SchemeCompiler.SchemeCompilationException e){
            e.invalidBlock.errorPaint();
            canvas.repaint();
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(parent, "Схема соответствует всем правилам.", "Сообщение", JOptionPane.INFORMATION_MESSAGE );


    }

    public void saveFile (){

        if(rootDiagramObject.getFirstSubObj()==null){
            JOptionPane.showMessageDialog(new Frame(), "Нельзя сохранить пустой файл", "Ошибка", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JSONObject jsonFile = createJSON();
            JFileChooser jFileChooser = new JFileChooser("C:\\Users\\L\\Documents\\Schemes\\");

            jFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.getName().endsWith("json")) return true;
                    return false;
                }

                @Override
                public String getDescription() {
                    return ".json";
                }
            });

            try {
                String dir;
                int rVal = jFileChooser.showSaveDialog(null);

                if (rVal == JFileChooser.APPROVE_OPTION) {
                    dir = jFileChooser.getCurrentDirectory().toString();

                    FileWriter file = new FileWriter(dir + "\\" + (jFileChooser.getSelectedFile().getName()) + ".json");
                    file.write(jsonFile.toJSONString());
                    file.flush();

                    getDiagramObject().caption = jFileChooser.getSelectedFile().getName();
                    AppStart.changeWindowTitle(getDiagramObject().caption);
                    labelExpression.setText("Открытая схема: " + getDiagramObject().caption);

                }

            } catch (InvalidPathException ex) {
                JOptionPane.showMessageDialog(new Frame(), "Введите корректный путь для сохранения файла", "Ошибка", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(new Frame(), "Невозможно сохранить файл с таким именем", "Ошибка", JOptionPane.INFORMATION_MESSAGE);
            }
    }



    public JSONObject createJSON() {
       DiagramObject currentObject = rootDiagramObject.getFirstSubObj();


        JSONArray blockArray = new JSONArray();
       JSONArray linkArray = new JSONArray();

       while(currentObject!=rootDiagramObject.getLastSubObj()){
           if (!currentObject.getClass().getName().contains("DiagramGeneralization")){
               blockArray.add(currentObject.getJSON());
           }
           else if (currentObject.getClass().getName().contains("DiagramGeneralization")){
               linkArray.add(currentObject.getJSON());
           }
           currentObject = currentObject.getNext();
       }
       try {
           if (rootDiagramObject.getLastSubObj().getClass().getName().equals("code.DiagramGeneralization"))
               linkArray.add(rootDiagramObject.getLastSubObj().getJSON());
           else blockArray.add(rootDiagramObject.getLastSubObj().getJSON());

           JSONObject jsonFile = new JSONObject();
           jsonFile.put("blocks", blockArray);
           jsonFile.put("links", linkArray);
           return jsonFile;
       }

       catch (NullPointerException e){
           return null;
       }

    }

    //парсинг файла JSON

    public void openJSONfile(String filepath) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try {
        FileReader reader = new FileReader(filepath);

            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray blockList = (JSONArray) jsonObject.get("blocks");
            JSONArray linkList = (JSONArray) jsonObject.get("links");

            Iterator blockIterator = blockList.iterator();
            Iterator linkIterator = linkList.iterator();

            Scheme newRootElement = new Scheme(true);
            newRootElement.diagramObjects.clear();
            setDiagramObject(newRootElement);

            while (blockIterator.hasNext()) {
                JSONObject currentObject = (JSONObject) blockIterator.next();
                String shape = currentObject.get("shape").toString();
                Class<?> aClass = Class.forName(shape);
                Constructor <?> constructor = aClass.getConstructor(double.class, double.class, String.class);
                Object object = constructor.newInstance(Double.valueOf(currentObject.get("x").toString()), Double.valueOf(currentObject.get("y").toString()), currentObject.get("text").toString());
                ((AbstractDiagramNode)object).setId(((Long)(currentObject.get("id"))).intValue());
                //System.out.println((currentObject.get("colorFill")).toString());
                ((AbstractDiagramNode)object).setColorFill(new Color(((Long)(currentObject.get("colorFillR"))).intValue(), ((Long)(currentObject.get("colorFillG"))).intValue(), ((Long)(currentObject.get("colorFillB"))).intValue()));
                ((AbstractDiagramNode)object).setColorBorder(new Color(((Long)(currentObject.get("colorBorderR"))).intValue(), ((Long)(currentObject.get("colorBorderG"))).intValue(), ((Long)(currentObject.get("colorBorderB"))).intValue()));
                ((AbstractDiagramNode)object).setColorFont(new Color(((Long)(currentObject.get("colorFontR"))).intValue(), ((Long)(currentObject.get("colorFontG"))).intValue(), ((Long)(currentObject.get("colorFontB"))).intValue()));

                newRootElement.addToQueue((DiagramObject)object);
                newRootElement.diagramObjects.put(((AbstractDiagramNode) object).getId(), (AbstractDiagramNode)object);
            }

             while (linkIterator.hasNext()) {
                 JSONObject currentObject = (JSONObject) linkIterator.next();
                 String shape = currentObject.get("shape").toString();
                 Integer idstart = ((Long) (currentObject.get("idstart"))).intValue();
                 Integer idend = ((Long) (currentObject.get("idend"))).intValue();
                 Integer id = ((Long) (currentObject.get("id"))).intValue();
                 AbstractDiagramNode nodeStart = (AbstractDiagramNode) newRootElement.diagramObjects.get(idstart);
                 AbstractDiagramNode nodeEnd = (AbstractDiagramNode) newRootElement.diagramObjects.get(idend);
                 Class<?> bClass = Class.forName(shape);
                 Constructor<?> constructor = bClass.getConstructor(AbstractDiagramNode.class, AbstractDiagramNode.class);
                 Object object = constructor.newInstance(nodeStart, nodeEnd);
                 ((AbstractDiagramLink) (object)).setId(id);
                 newRootElement.addToQueue(((AbstractDiagramLink) object));
                 newRootElement.diagramObjects.put(((AbstractDiagramLink) object).getId(), (AbstractDiagramLink) object);
             }

             selection.clear();
                canvas.repaint();


        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(new Frame(), "Выбранный файл не существует или был удалён", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new Frame(), "Файл повреждён или несовместим с данным программным обеспечением", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(new Frame(), "Файл повреждён или несовместим с данным программным обеспечением", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new Frame(), "Файл повреждён или несовместим с данным программным обеспечением", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchMethodException e) {
            JOptionPane.showMessageDialog(new Frame(), "Файл повреждён или несовместим с данным программным обеспечением", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalAccessException e) {
            JOptionPane.showMessageDialog(new Frame(), "Файл повреждён или несовместим с данным программным обеспечением", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (InstantiationException e) {
            JOptionPane.showMessageDialog(new Frame(), "Файл повреждён или несовместим с данным программным обеспечением", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (InvocationTargetException e) {
            JOptionPane.showMessageDialog(new Frame(), "Файл повреждён или несовместим с данным программным обеспечением", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static int round(double val) {
        return (int) Math.round(val);
    }

    public void canvasMouseDown(MouseEvent e) {
        if (rootDiagramObject == null)
            return;

        //клик на холст закрывает текущее окно свойств
        if (contextFrame!= null) contextFrame.dispose();

        Point cursorPos = MouseInfo.getPointerInfo().getLocation();
        if (panningMode) {
            // set mouse cursor to 'closed hand'
            // Screen.Cursor = crClHand;
            startPoint.x = round(hsb.getValue() / SCROLL_FACTOR + cursorPos.x / scale);
            startPoint.y = round(vsb.getValue() / SCROLL_FACTOR + cursorPos.y / scale);
        }
        //правая кнопка мыши по объекту вызывает окно свойств объекта
        else if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
            creatingLink = false;
            canvas.repaint();
            SwingUtilities.convertPointFromScreen(cursorPos, canvas);
            startPoint.setLocation(cursorPos);
            currentPoint.setLocation(startPoint);
            DiagramObject diagramObject = rootDiagramObject.testHit(currentPoint.x, currentPoint.y);
            if ((diagramObject != null)) {
                selection.clear();
                selection.mouseDown(diagramObject, false);
                ((AbstractDiagramNode)diagramObject).errorPaintReset();
                mouseDown = true;

                contextFrame = new ContextFrame("Свойства " + diagramObject.getCaption(), diagramObject, canvas);
            }
        }

        //создание блока, выбранного на панели объектов
        else if (creatingBlock!= null){

            SwingUtilities.convertPointFromScreen(cursorPos, canvas);
            startPoint.setLocation(cursorPos);
            currentPoint.setLocation(startPoint);
            Constructor <?> constructor = null;
            try {
                constructor = creatingBlock.getConstructor(double.class, double.class, String.class);
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            Object object = null;
            Point p = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(p, canvas);
            try {
                object = constructor.newInstance(currentPoint.x / scale + rootDiagramObject.getDX(), currentPoint.y / scale + rootDiagramObject.getDY(), "");
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }

            if(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size()!=0) {
                Set<Integer> keys = ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.keySet();
                ((DiagramObject) (object)).setId(Collections.max(keys) + 1);
            }
            else { ((DiagramObject) (object)).setId(1); }

            rootDiagramObject.addToQueue(((DiagramObject)object));
            ((Scheme)(rootDiagramObject)).diagramObjects.put(((AbstractDiagramNode) object).getId(), (AbstractDiagramNode)object);

            creatingBlock = null;
            pressed.setSelected(false);
            canvas.repaint();
        }
        //создание связи
        else if(creatingLink){
            SwingUtilities.convertPointFromScreen(cursorPos, canvas);
            startPoint.setLocation(cursorPos);
            currentPoint.setLocation(startPoint);

            DiagramObject diagramObject = rootDiagramObject.testHit(currentPoint.x, currentPoint.y);

            if ((diagramObject != null)) {
              if(AbstractDiagramNode.class.isAssignableFrom(diagramObject.getClass())){
                  AbstractDiagramNode node = (AbstractDiagramNode)diagramObject;
                  if(node == ContextFrame.item){
                      abortLinkCreation("Блоки не могут замыкаться на самих себя");
                      return;
                  }
                  if(!creatingFromContextNode && node.getClass().equals(DiagramTerminatorEnd.class)){
                      abortLinkCreation("Блок конца не может иметь исходяших связей");
                      return;
                  }
                  if(creatingFromContextNode && node.getClass().equals(DiagramTerminatorStart.class)){
                      abortLinkCreation("Блок начала не может иметь входящих связей");
                      return;
                  }
                  if(!creatingFromContextNode && !node.getClass().equals(DiagramRhombus.class) && node.get_lines_out().size()>0){
                      abortLinkCreation("Блок " + node.getName()  + " не может иметь больше исходящих связей");
                      return;
                  }
                  if(creatingFromContextNode && ContextFrame.item.getClass().equals(DiagramRhombus.class)){
                      if(ContextFrame.item.get_lines_out().size() != 0 && ContextFrame.item.get_lines_out().get(0).nTo == node){
                          abortLinkCreation("Выходы из блока условий " + node.getName()  + "должны вести к разным блокам");
                          return;
                      }
                  }
                  if(!creatingFromContextNode && node.getClass().equals(DiagramRhombus.class)){
                      if(node.get_lines_out().size() != 0 && node.get_lines_out().get(0).nTo == ContextFrame.item){
                          abortLinkCreation("Выходы из блока условий " + node.getName()  + "должны вести к разным блокам");
                          return;
                      }
                  }
                  if(!creatingFromContextNode && node.getClass().equals(DiagramRhombus.class) && node.get_lines_out().size()>1){
                      abortLinkCreation("Блок " + node.getName()  + "не может иметь больше исходящих связей");
                      return;
                  }

                  DiagramGeneralization diagramGeneralization;
                  if (creatingFromContextNode) {
                       diagramGeneralization = new DiagramGeneralization(ContextFrame.item, node);
                  }
                  else{
                       diagramGeneralization = new DiagramGeneralization(node, ContextFrame.item);
                  }
                  Set<Integer> keys = ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.keySet();
                  diagramGeneralization.setId(Collections.max(keys) + 1);
                  ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.put(((AbstractDiagramLink) diagramGeneralization).getId(), (AbstractDiagramLink) diagramGeneralization);
                  rootDiagramObject.addToQueue(diagramGeneralization);
                  creatingLink = false;
                  canvas.repaint();
              }

              else
              {
                  creatingLink = false;
              }

            }
        } else {
            SwingUtilities.convertPointFromScreen(cursorPos, canvas);
            startPoint.setLocation(cursorPos);
            currentPoint.setLocation(startPoint);
            selection.mouseDown(rootDiagramObject.testHit(currentPoint.x, currentPoint.y),
                    (e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) > 0);
            mouseDown = true;

        }
    }

    void abortLinkCreation(String str){
        JOptionPane.showMessageDialog(new Frame(), str, "Ошибка", JOptionPane.ERROR_MESSAGE);
        creatingLink = false;
        canvas.repaint();
    }

    private void canvasMouseMoved(MouseEvent e) {
        if (rootDiagramObject == null)
            return;
        currentElement = rootDiagramObject.testHit(e.getX(), e.getY());

        if(creatingLink) {

            Point cursorPos = MouseInfo.getPointerInfo().getLocation();
            strokeLine.draw(canvas.getGraphics(), 0, 0, scale);
            strokeLine.drawSelection(currentPoint.x, currentPoint.y);
            SwingUtilities.convertPointFromScreen(cursorPos, canvas);
            currentPoint.setLocation(cursorPos);
            strokeLine.draw(canvas.getGraphics(), 0, 0, scale);
            strokeLine.drawSelection(currentPoint.x, currentPoint.y);

            hintLabel.setText("Теперь нажмите на блок, с которым хотите создать связь");
        }
        else if (currentElement == null) {
            String h = rootDiagramObject.getHint();
            hintLabel.setText(h);
        } else {
            String h = currentElement.getHint();
            hintLabel.setText(h);
        }
    }

    private void canvasMouseDragged(MouseEvent e) {
        if (rootDiagramObject == null)
            return;
        Point cursorPos = MouseInfo.getPointerInfo().getLocation();
        if (panningMode || SwingUtilities.isMiddleMouseButton(e)) {
            System.out.println("dragging");
            // передвижение картинки как единого целого
            hsb.setValue(round((startPoint.x - cursorPos.x / scale) * SCROLL_FACTOR));
            vsb.setValue(round((startPoint.y - cursorPos.y / scale) * SCROLL_FACTOR));

        } else {
            if (!mouseDown)
                return;
            // передвижение одного объекта на картинке
            selection.mouseMove(currentPoint.x - startPoint.x, currentPoint.y - startPoint.y);
            SwingUtilities.convertPointFromScreen(cursorPos, canvas);
            currentPoint.setLocation(cursorPos);
            selection.mouseMove(currentPoint.x - startPoint.x, currentPoint.y - startPoint.y);
        }
    }

    private void canvasMouseUp(MouseEvent e) {
        if (rootDiagramObject == null)
            return;

        if (panningMode) {
            // TODO
            // set mouse cursor to 'open palm'
        } else {
            if (!mouseDown)
                return;

            Point p = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(p, canvas);

            int dX = p.x - startPoint.x;
            int dY = p.y - startPoint.y;
            selection.mouseMove(currentPoint.x - startPoint.x, currentPoint.y - startPoint.y);
            if ((e.getX() > 0) && (e.getY() > 0) && (e.getX() < canvas.getWidth()) && (e.getY() < canvas.getHeight())) {
                selection.mouseUp(dX, dY);
                canvas.paint(canvas.getGraphics());
                selection.mouseMove(0, 0);
                startPoint.setLocation(e.getX(), e.getY());
                currentPoint.setLocation(e.getX(), e.getY());
            }
            mouseDown = false;
        }

    }

    private void canvasMouseWheel(MouseWheelEvent e) {
        if ((e.getModifiersEx() & MouseWheelEvent.CTRL_DOWN_MASK) > 0) {
            setScale(scale * Math.exp(-e.getPreciseWheelRotation() / WHEEL_FACTOR));
        } else {
            Point clientPos = e.getPoint();
            if ((e.getModifiersEx() & MouseWheelEvent.SHIFT_DOWN_MASK) > 0) {
                int newPos = hsb.getValue() + round(e.getPreciseWheelRotation() * SCROLL_FACTOR * 20);
                if (newPos < 0) {
                    hsb.setValue(0);
                } else {
                    hsb.setValue(newPos);
                }
            } else {
                int newPos = vsb.getValue() + round(e.getPreciseWheelRotation() * SCROLL_FACTOR * 20);
                if (newPos < 0) {
                    vsb.setValue(0);
                } else {
                    vsb.setValue(newPos);
                }
            }
        }
    }

    private void scrollBarChange() {
        canvas.paint(canvas.getGraphics());
        // чтобы визуально не слетело выделение
        if (selection.items.size() > 0)
            selection.mouseMove(0, 0);
    }

    /**
     * Returns the root painter object.
     */
    public static DiagramObject getDiagramObject() {
        return rootDiagramObject;
    }

    /**
     * Sets the zoom factor.
     *
     * @param s
     *            new zoom factor
     */
    public void setScale(double s) {
        if (s < 0.05 || s > 100 || s == scale)
            return;
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, canvas);

        double xQuot;
        double yQuot;
        if (p.x > 0 && p.y > 0 && p.x < canvas.getWidth() && p.y < canvas.getHeight()) {
            xQuot = p.getX() / (double) canvas.getWidth();
            yQuot = p.getY() / (double) canvas.getHeight();
        } else {
            xQuot = 0.5;
            yQuot = 0.5;
        }
        int newHVal = hsb.getValue() + round(hsb.getVisibleAmount() * xQuot * (1 - scale / s));
        int newVVal = vsb.getValue() + round(vsb.getVisibleAmount() * yQuot * (1 - scale / s));
        // Сохраняем неподвижной точку, на которой находится указатель мыши
        // или визуальный центр диаграммы, если нажаты экранные кнопки
        hsb.setValue(newHVal);
        vsb.setValue(newVVal);

        scale = s;
        canvas.paint(canvas.getGraphics());
        // А это --- чтоб визуально не слетело выделение.
        if (!selection.getItems().isEmpty())
            selection.mouseMove(0, 0);
    }

    /**
     * Zooms in ('+' clicked).
     */
    public void zoomIn() {
        setScale(scale * ZOOM_FACTOR); // sqrt(2)
        canvas.paint(canvas.getGraphics());
    }

    /**
     * Zooms out ('-' clicked).
     */
    public void zoomOut() {
        setScale(scale / ZOOM_FACTOR);
        canvas.paint(canvas.getGraphics());// sqrt(2)
    }

    /**
     * Switches between "panning" and "selection" modes.
     *
     *            true for "panning" mode.
     */
    public void setPanningMode(boolean dragMode) {
        this.panningMode = dragMode;
        handButton.setSelected(dragMode);
        cursorButton.setSelected(!dragMode);
        if (dragMode)
            canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else {
            canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Sets the root painting object and repaints the diagram.
     *
     * @param diagramObject
     *            new root painting object
     */

    public void setDiagramObject(DiagramObject diagramObject) {
        this.rootDiagramObject = diagramObject;
        double worldHeight = diagramObject.getMaxY() - diagramObject.getMinY();
        double worldWidth = diagramObject.getMaxX() - diagramObject.getMinX();

        vsb.setMaximum(round(worldHeight * SCROLL_FACTOR));
        hsb.setMaximum(round(worldWidth * SCROLL_FACTOR));

        selection.clear();
        canvas.repaint();
    }


    /**
     * Possible states during mouse moving.
     */
    public enum State {
        SELECTING, LASSO, DRAGGING
    };



    /**
     * Incapsulates objects selection functionality.
     */

public class SelectionManager {

    private final ArrayList<DiagramObject> items = new ArrayList<DiagramObject>();

    private final Consumer<DiagramObject> collector = (item) -> {
        if (!items.contains(item))
            items.add(item);
    };

    private DiagramObject nonMoveable;
    private DiagramPanel.State state = DiagramPanel.State.SELECTING;

    private List<DiagramObject> getItems() {
        return Collections.unmodifiableList(items);
    }

    private void clear() {
        items.clear();
    }

    //клик на холст

    void mouseDown(DiagramObject item, boolean shiftKey) {
        /*
         * Эта строчка ничего не отрисовывает, но нужна для того, чтобы
         * снабдить лассо правильным Canvas'ом
         */
        lasso.draw(canvas.getGraphics(), 0, 0, scale);
        /*
         * Если по какой-либо причине событие MouseUp не наступило,
         * искусственно вызываем его, чтобы избежать ошибок
         */
        if (state != DiagramPanel.State.SELECTING)
            mouseUp(0, 0);


        assert state == DiagramPanel.State.SELECTING;

        int i = items.indexOf(item);

        /*
         * Режим без Ctrl, Shift: - если ткнули в выделенный объект, то
         * делаем его первым в списке выделенных - если мы ткнули в пустое
         * место, то просто сбрасываем выделение, - если ткнули в
         * невыделенный объект, то сбрасываем выделение и выделяем кликнутый
         * объект.
         */

        if (!shiftKey) {
            if (i >= 0) {
                Collections.swap(items, i, 0);
            } else {
                items.clear();
                if (item != null)
                    items.add(item);
            }

        } else {
            /*
             * Режим с Ctrl, Shift: - если ткнули в выделенный объект, то
             * удаляем его из выделения - если ткнули в невыделенный объект,
             * то добавлем его к выделению и делаем первым
             */
            if (i >= 0)
                items.remove(i);
            else if (item != null) {
                items.add(item);
                Collections.swap(items, 0, items.size() - 1);
            }
        }

        /*
         * Если ткнули в пустое место --- переходим к построению лассо
         */
        if (item == null) {
            state = DiagramPanel.State.LASSO;
            nonMoveable = null;
        } else if (!item.isMoveable()) {

            /*
             * Иначе, если ткнули в неподвижный объект --- переходим к
             * построению лассо
             */

            state = DiagramPanel.State.LASSO;
            nonMoveable = item;

        } else {
            /*
             * Иначе --- переходим к перемещению выделенных объектов
             */
            state = DiagramPanel.State.DRAGGING;
        }


    }

    void mouseMove(int dX, int dY) {
        switch (state) {
            // Режим отрисовки лассо
            case LASSO:
                /*
                 * Эта строчка ничего не отрисовывает, но нужна для того, чтобы
                 * снабдить лассо правильным Canvas'ом
                 */
                lasso.draw(canvas.getGraphics(), 0, 0, scale);
                lasso.drawSelection(dX, dY);
                break;
            // Режим передвижения объектов
            default:
                for (DiagramObject i : items) {
                    assert i != null;
                    i.drawSelection(dX, dY);
                }
        }
    }

    private void internalDrop(int dX, int dY) {
        if ((!items.isEmpty()) && ((dX != 0) || (dY != 0))) {

            for (DiagramObject i : items) {
                DiagramObject curItem = i.getParent();
                while (curItem != null && !items.contains(curItem)) {
                    curItem = curItem.getParent();
                }
                if (curItem == null)
                    i.drop(dX, dY);
            }
        }
    }

    void mouseUp(int dX, int dY) {
        switch (state) {
            case LASSO:
                if ((dX != 0) || (dY != 0)) {
                    items.remove(nonMoveable);
                    nonMoveable = null;
                }
                lasso.drop(dX, dY);
                break;
            case DRAGGING:

                internalDrop(dX, dY);
                canvas.paint(canvas.getGraphics());
                break;
            default:
                break;
        }
        state = DiagramPanel.State.SELECTING;
    }
}

public class Lasso extends DiagramObject {
    @Override
    protected void internalDrawSelection(int dX, int dY) {

        getCanvas().setColor(Color.BLACK);
        getCanvas().setXORMode(Color.WHITE);

        Graphics2D g2 = (Graphics2D) getCanvas();

        float dash1[] = { 5.0f };
        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
        Stroke s = g2.getStroke();
        g2.setStroke(dashed);

        int x0 = dX > 0 ? startPoint.x : startPoint.x + dX;
        int y0 = dY > 0 ? startPoint.y : startPoint.y + dY;

        g2.drawRect(x0, y0, Math.abs(dX), Math.abs(dY));
        g2.setStroke(s);

        getCanvas().setPaintMode();
    }

    @Override
    protected void internalDrop(double dX, double dY) {
        int minX;
        int minY;

        dX = dX * scale;
        dY = dY * scale;

        if (dX < 0) {
            minX = startPoint.x + round(dX);
            dX = -dX;
        } else {
            minX = startPoint.x;
        }

        if (dY < 0) {
            minY = startPoint.y + round(dY);
            dY = -dY;
        } else {
            minY = startPoint.y;
        }
        rootDiagramObject.collect(minX, minY, minX + round(dX), minY + round(dY), selection.collector);
    }
}

public class StrokeLine extends  DiagramObject{

    @Override
    protected void internalDrawSelection(int dX, int dY) {

        getCanvas().setColor(Color.BLACK);
        getCanvas().setXORMode(Color.WHITE);

        Graphics2D g2 = (Graphics2D) getCanvas();

        float dash1[] = { 5.0f };
        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
        Stroke s = g2.getStroke();
        g2.setStroke(dashed);

        g2.drawLine((int)(ContextFrame.item.getmX() * scale - rootDiagramObject.getDX()), (int)(ContextFrame.item.getmY() * scale - rootDiagramObject.getDY()), Math.abs(dX), Math.abs(dY));
        g2.setStroke(s);

        getCanvas().setPaintMode();
    }
}


public class DiagramCanvas extends Canvas{

        private static final long serialVersionUID = 1L;

        DiagramCanvas() {
            super();
        }


        @Override
        public void paint(Graphics g) {
            // not ready for painting yet
            if (rootDiagramObject == null || g == null)
                return;
            double worldHeight = rootDiagramObject.getMaxY() - rootDiagramObject.getMinY();
            double worldWidth = rootDiagramObject.getMaxX() - rootDiagramObject.getMinX();
            int hPageSize = round(canvas.getWidth() / scale);

            if (hPageSize > worldWidth) {
                hsb.setValue(0);
                hsb.setVisibleAmount(round(worldWidth * SCROLL_FACTOR));
            } else {
                hsb.setVisibleAmount(round(hPageSize * SCROLL_FACTOR));
            }
            int vPageSize = round(canvas.getHeight() / scale);
            if (vPageSize > worldHeight) {
                vsb.setValue(0);
                vsb.setVisibleAmount(round(worldHeight * SCROLL_FACTOR));
            } else {
                vsb.setVisibleAmount(round(vPageSize * SCROLL_FACTOR));
            }

            hsb.setMaximum(round(worldWidth * SCROLL_FACTOR));
            vsb.setMaximum(round(worldHeight * SCROLL_FACTOR));

            // setupLargeChange(hsb);
            // setupLargeChange(vsb);
            g.clearRect(0, 0, getWidth(), getHeight());

            double dX;
            if (hPageSize > worldWidth) {
                dX = (worldWidth - hPageSize) / 2;
            } else {
                dX = hsb.getValue() / SCROLL_FACTOR;
            }

            double dY;
            if (vPageSize > worldHeight) {
                dY = (worldHeight - vPageSize) / 2;
            } else {
                dY = vsb.getValue() / SCROLL_FACTOR;
            }

            rootDiagramObject.draw(g, dX, dY, scale);

        }

    }

    /**
     * The element currently under mouse cursor.
     */
    public DiagramObject getCurrentElement() {
        return currentElement;
    }

    /**
     * The list of selected elements.
     */
    public List<DiagramObject> getSelection() {
        return selection.getItems();
    }

}



import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PlotPane extends ApplicationFrame{
    final XYSeriesCollection data = new XYSeriesCollection();
    public PlotPane(String title){
        //Дефолтный конструктор, не отображает данные
        super(title);
    }


    private void setSeries(XYSeries series,ArrayList<Point2D.Float> values){
        for (int i = 0; i < values.size(); ++i)
            series.add(values.get(i).x,values.get(i).y);
    }

    public void addData(String title,ArrayList<Point2D.Float> values){
        XYSeries newSeries = new XYSeries(title);
        setSeries(newSeries,values);
        data.addSeries(newSeries);
    }

    public void plot(String title){
        final JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "X",
                "Y",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 400));
        setContentPane(chartPanel);
    }

}

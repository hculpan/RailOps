package org.culpan.railops.util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.culpan.railops.dao.CarsDao;
import org.culpan.railops.dao.LocationsDao;
import org.culpan.railops.dao.SwitchListDao;
import org.culpan.railops.model.Car;
import org.culpan.railops.model.Move;
import org.culpan.railops.model.Route;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ReportGenerator {
    private final static CarsDao carsDao = new CarsDao();
    private final static SwitchListDao switchListDao = new SwitchListDao();
    private final static LocationsDao locationsDao = new LocationsDao();

    private final List<Object> data = new ArrayList<>();

    public ReportGenerator() { }

    public ReportGenerator(Object...data) {
        setData(data);
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(Object...data) {
        this.data.clear();
        for (Object o : data) {
            this.data.add(o);
        }
    }

    public static void showReport(File dest, Function<File, Boolean> report) {
        if (report.apply(dest)) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(dest);
            } catch (IOException e) {
                AppHelper.showExceptionInfo(e, e.getLocalizedMessage());
            }
        }

    }

    public boolean reportSwitchList(File dest) {
        if (data.size() != 2) return false;

        boolean result = false;
        Route route = (Route)data.get(0);
        List<Move> moves = (List<Move>)data.get(1);

        PdfWriter writer = null;
        try {
            writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            Text title = new Text("Switch List").setFont(bold).setFontSize(24);
            Paragraph p = new Paragraph().add(title);
            p.setTextAlignment(TextAlignment.CENTER);
            document.add(p);

            Text line = new Text(String.format("Scheduled work for %s", route.getDisplayName())).setFont(font).setFontSize(12);
            document.add(new Paragraph().add(line));

            for (Move m : moves) {
                document.add(reportMove(m, font));
            }

            document.close();
            result = true;
        } catch (IOException e) {
            AppHelper.showExceptionInfo(e, e.getLocalizedMessage());
        }
        return result;
    }

    private Paragraph reportMove(Move m, PdfFont font) {
        Paragraph result = new Paragraph();

        String line = "";

        if (m.getLocation() == null && m.getLocationId() > 0) {
            m.setLocation(locationsDao.findById(m.getLocationId()));
        }

        switch (m.getActionType()) {
            case atPickupLocomotive:
                line = String.format("Departs %s", m.getLocation().getName());
                break;
            case atDropoffLocmotive:
                line = String.format("Arrives at %s", m.getLocation().getName());
                break;
            case atPickUp:
                Car car = carsDao.findById(m.getCarId());
                line = String.format("[ ] Pick up %s %s (%s) at %s",
                        car.getRoadMark(),
                        car.getRoadId(),
                        car.getAarCode(),
                        m.getLocation().getName());
                break;
            case atSetOut:
                car = carsDao.findById(m.getCarId());
                line = String.format("[ ] Set out %s %s (%s) to %s",
                        car.getRoadMark(),
                        car.getRoadId(),
                        car.getAarCode(),
                        m.getLocation().getName());
                break;
        }

        result.add(new Text(line).setFont(font).setFontSize(12));

        return result;
    }

    public boolean reportFreightCars(File dest) {
        boolean result = false;
        PdfWriter writer = null;
        try {
            writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            Text title = new Text("Freight Cars").setFont(bold).setFontSize(24);
            Paragraph p = new Paragraph().add(title);
            p.setTextAlignment(TextAlignment.CENTER);
            document.add(p);

            Table table = new Table(new float[]{0.5f, 0.5f, 3, 4, 4});
            table.setWidth(UnitValue.createPercentValue(100));

            List<Car> cars = carsDao.load();
            int index = 0;
            process(table, null, bold, true, index++);
            for (Car car : cars) {
                process(table, car, font, false, index++);
            }
            document.add(table);
            document.close();
            result = true;
        } catch (IOException e) {
            AppHelper.showExceptionInfo(e, e.getLocalizedMessage());
        }
        return result;
    }

    private void process(Table table, Car car, PdfFont font, boolean isHeader, int index) {
        if (isHeader) {
            table.addHeaderCell(createCell("Road Mark", font, isHeader, index));
            table.addHeaderCell(createCell("Road ID", font, isHeader, index));
            table.addHeaderCell(createCell("AAR Code", font, isHeader, index));
            table.addHeaderCell(createCell("Kind", font, isHeader, index));
            table.addHeaderCell(createCell("Current Location", font, isHeader, index));
        } else {
            table.addCell(createCell(car.getRoadMark(), font, isHeader, index));
            table.addCell(createCell(car.getRoadId(), font, isHeader, index));
            table.addCell(createCell(car.getAarCode(), font, isHeader, index));
            table.addCell(createCell(car.getKind(), font, isHeader, index));
            table.addCell(createCell((car.getLocation() != null ?
                    car.getLocation().getName() : ""), font, isHeader, index));
        }
    }

    private Cell createCell(String text, PdfFont font, boolean isHeader, int index) {
        Cell result = new Cell().add(new Paragraph(text).setFont(font));
        result.setFontColor(ColorConstants.BLACK);
        if (isHeader) {
            result.setBackgroundColor(ColorConstants.DARK_GRAY);
            result.setFontColor(ColorConstants.WHITE);
        } else if (index % 2 == 0) {
            result.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        }
        result.setBorder(new SolidBorder(ColorConstants.WHITE, 0));
        return result;
    }
}

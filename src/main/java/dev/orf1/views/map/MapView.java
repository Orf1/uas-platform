package dev.orf1.views.map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import dev.orf1.views.dashboard.ServiceHealth;
import jakarta.annotation.security.PermitAll;

@PageTitle("Map")
@Menu(icon = "line-awesome/svg/map-marked-alt-solid.svg", order = 1)
@Route(value = "map")
@PermitAll
public class MapView extends Composite<VerticalLayout> {

    public MapView() {
        getContent().setSizeFull();
        getContent().getStyle().set("flex-grow", "1");

        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();

        layoutRow.setSizeFull();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.getStyle().set("flex-grow", "1");

        Map map = new Map();
        Map.setUserProjection("EPSG:3857");
        map.setCenter(new Coordinate(-13560743.332938, 4653664.270371));
        map.setZoom(16);

        Coordinate home = new Coordinate(-13560148.854167,4653598.548887);

        Coordinate a = new Coordinate(-13561121.032956, 4653745.451301);
        Coordinate b = new Coordinate(-13561130.027743, 4653407.456316);
        Coordinate c = new Coordinate(-13560307.419946, 4653395.583011);
        Coordinate d = new Coordinate(-13560311.917340,4653723.673466);


        MarkerFeature homeFeature = new MarkerFeature(home);
        map.getFeatureLayer().addFeature(homeFeature);
        MarkerFeature aFeature = new MarkerFeature(a);
        map.getFeatureLayer().addFeature(aFeature);
        MarkerFeature bFeature = new MarkerFeature(b);
        map.getFeatureLayer().addFeature(bFeature);
        MarkerFeature cFeature = new MarkerFeature(c);
        map.getFeatureLayer().addFeature(cFeature);
        MarkerFeature dFeature = new MarkerFeature(d);
        map.getFeatureLayer().addFeature(dFeature);

        map.setSizeFull();
        layoutColumn2.setSizeFull();
        layoutColumn2.add(map);

        H1 h1 = new H1("Tools");
        Button emergencyStop = new Button("Stop System");
        emergencyStop.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        layoutColumn3.add(createServiceHealth(), createResponseTimes());

        // Add layouts to the row
        layoutRow.add(layoutColumn2);
        layoutRow.add(layoutColumn3);

        // Add the row to the root layout
        getContent().add(layoutRow);
    }

    private Component createResponseTimes() {
        HorizontalLayout header = createHeader("Data Overview", "Average across all systems");

        // Chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        chart.setThemeName("gradient");

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("NDVI (Plant Health)", 0.75));
        series.add(new DataSeriesItem("Soil Moisture (%)", 45.3));
        series.add(new DataSeriesItem("Crop Height (cm)", 150.0));
        series.add(new DataSeriesItem("Canopy Cover (%)", 80.2));
        series.add(new DataSeriesItem("Thermal Imaging (°C)", 28.5));
        series.add(new DataSeriesItem("Weed Density (count/m²)", 12));

        conf.addSeries(series);

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, chart);
        serviceHealth.addClassName(LumoUtility.Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    private Component createServiceHealth() {
        // Header
        HorizontalLayout header = createHeader("UAV Fleet Health", "Active");

        // Grid
        Grid<ServiceHealth> grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        grid.addColumn(new ComponentRenderer<>(serviceHealth -> {
            Span status = new Span();
            String statusText = getStatusDisplayName(serviceHealth);
            status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().setAttribute("title", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(serviceHealth));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(ServiceHealth::getCity).setHeader("Vehicle").setFlexGrow(1);
        grid.addColumn(ServiceHealth::getInput).setHeader("Scanned").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(ServiceHealth::getOutput).setHeader("Remaining").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.setItems(new ServiceHealth(ServiceHealth.Status.EXCELLENT, "UAV A (Active)", 324, 1540),
                new ServiceHealth(ServiceHealth.Status.OK, "UAV B (Active)", 311, 1320),
                new ServiceHealth(ServiceHealth.Status.FAILING, "UAV C (Charging)", 0, 0));

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
        serviceHealth.addClassName(LumoUtility.Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private String getStatusDisplayName(ServiceHealth serviceHealth) {
        ServiceHealth.Status status = serviceHealth.getStatus();
        if (status == ServiceHealth.Status.OK) {
            return "Ok";
        } else if (status == ServiceHealth.Status.FAILING) {
            return "Failing";
        } else if (status == ServiceHealth.Status.EXCELLENT) {
            return "Excellent";
        } else {
            return status.toString();
        }
    }

    private String getStatusTheme(ServiceHealth serviceHealth) {
        ServiceHealth.Status status = serviceHealth.getStatus();
        String theme = "badge primary small";
        if (status == ServiceHealth.Status.EXCELLENT) {
            theme += " success";
        } else if (status == ServiceHealth.Status.FAILING) {
            theme += " error";
        }
        return theme;
    }
}

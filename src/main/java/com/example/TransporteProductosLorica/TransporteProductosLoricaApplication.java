package com.example.TransporteProductosLorica;

import com.example.TransporteProductosLorica.Modelo.Producto;
import com.example.TransporteProductosLorica.Modelo.Ruta;
import com.example.TransporteProductosLorica.Modelo.Transporte;
import com.example.TransporteProductosLorica.Modelo.Vehiculo;
import com.example.TransporteProductosLorica.Servicio.ProductoServicio;
import com.example.TransporteProductosLorica.Servicio.RutaServicio;
import com.example.TransporteProductosLorica.Servicio.TransporteServicio;
import com.example.TransporteProductosLorica.Servicio.VehiculoServicio;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;


@SpringBootApplication
public class TransporteProductosLoricaApplication implements CommandLineRunner {

	@Autowired private VehiculoServicio vehiculoServicio;
	@Autowired private ProductoServicio productoServicio;
	@Autowired private RutaServicio rutaServicio;
	@Autowired private TransporteServicio transporteServicio;

	public static void main(String[] args) {
		SpringApplication.run(TransporteProductosLoricaApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Scanner scanner = new Scanner(System.in);

		boolean hayDatos = productoServicio.contarProductos() > 0 ||
				vehiculoServicio.contarVehiculos() > 0 ||
				rutaServicio.contarRutas() > 0;

		if (hayDatos) {
			System.out.println("Ya existen datos registrados en el sistema.");
			System.out.print("¿Desea continuar con el menú principal? (s/n): ");
			String continuar = scanner.nextLine().trim().toLowerCase();

			if (!continuar.equals("s")) {
				System.out.print("¿Desea ver los datos existentes (productos, rutas o vehículos)? (s/n): ");
				String verDatos = scanner.nextLine().trim().toLowerCase();

				if (verDatos.equals("s")) {
					mostrarDatosExistentes(scanner);
				} else {
					System.out.println("Programa finalizado por el usuario.");
					scanner.close();
					return;
				}
			}
		} else {
			System.out.print("¿Desea insertar los datos iniciales de productos, vehículos y rutas? (s/n): ");
			String respuesta = scanner.nextLine().trim().toLowerCase();
			if (respuesta.equals("s")) {
				insertarDatosIniciales();
				System.out.println("Datos iniciales cargados correctamente.\n");
			}
		}

		boolean continuar = true;
		while (continuar) {
			mostrarMenuPrincipal();
			int opcion = scanner.nextInt();
			scanner.nextLine(); // consumir salto

			try {
				switch (opcion) {
					case 1 -> agregarVehiculo(scanner);
					case 2 -> agregarProducto(scanner);
					case 3 -> agregarRuta(scanner);
					case 4 -> optimizarTransporte(scanner);
					case 5 -> mostrarEstadisticasGenerales();
					case 6 -> continuar = false;
					default -> System.out.println("Opción inválida.");
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}

		scanner.close();
		System.out.println("\n¡Gracias por usar el sistema de transporte agrícola Lorica!");
	}

	private void mostrarDatosExistentes(Scanner scanner) {
		boolean verMenu = true;
		while (verMenu) {
			System.out.println("\n--- ¿Qué desea ver? ---");
			System.out.println("1. Productos");
			System.out.println("2. Rutas");
			System.out.println("3. Vehículos");
			System.out.println("4. Volver al menú principal");
			System.out.println("5. Salir");
			System.out.print("Seleccione una opción: ");
			int opcionVer = scanner.nextInt();
			scanner.nextLine();

			switch (opcionVer) {
				case 1 -> {
					List<Producto> productos = productoServicio.listarTodos();
					if (productos.isEmpty()) System.out.println("No hay productos registrados.");
					else productos.forEach(System.out::println);
				}
				case 2 -> {
					List<Ruta> rutas = rutaServicio.listarHabilitadas();
					if (rutas.isEmpty()) System.out.println("No hay rutas registradas.");
					else rutas.forEach(System.out::println);
				}
				case 3 -> {
					List<Vehiculo> vehiculos = vehiculoServicio.listarTodos();
					if (vehiculos.isEmpty()) System.out.println("No hay vehículos registrados.");
					else vehiculos.forEach(System.out::println);
				}
				case 4 -> verMenu = false;
				case 5 -> {
					System.out.println("Programa finalizado por el usuario.");
					scanner.close();
					System.exit(0);
				}
				default -> System.out.println("Opción inválida.");
			}
		}
	}

	private void insertarDatosIniciales() {
		if (productoServicio.contarProductos() == 0) {
			productoServicio.crearProducto(new Producto("Yuca", Producto.TipoCarga.NO_PERECEDERO, "Pesada, gran volumen, poco perecedera"));
			productoServicio.crearProducto(new Producto("Plátano", Producto.TipoCarga.PERECEDERO, "Requiere cuidado en manejo"));
			productoServicio.crearProducto(new Producto("Ñame", Producto.TipoCarga.NO_PERECEDERO, "Pesado y requiere transporte a granel"));
			productoServicio.crearProducto(new Producto("Maíz", Producto.TipoCarga.GRANO, "Transportado seco, moderado volumen"));
			productoServicio.crearProducto(new Producto("Guayaba", Producto.TipoCarga.FRUTA_FRESCA, "Fruta perecedera, requiere protección"));
			productoServicio.crearProducto(new Producto("Papaya", Producto.TipoCarga.FRUTA_FRESCA, "Fruta sensible al daño y temperatura"));
			productoServicio.crearProducto(new Producto("Ají dulce", Producto.TipoCarga.PERECEDERO, "Muy perecedero, requiere transporte rápido"));
			productoServicio.crearProducto(new Producto("Cilantro", Producto.TipoCarga.PERECEDERO, "Fresco, requiere rapidez y ventilación"));
			productoServicio.crearProducto(new Producto("Aguacate", Producto.TipoCarga.FRUTA_FRESCA, "Requiere manejo cuidadoso y protección"));
			productoServicio.crearProducto(new Producto("Limón Tahití", Producto.TipoCarga.PERECEDERO, "Requiere ventilación, perece a largo transporte"));
		}


		if (vehiculoServicio.contarVehiculos() == 0) {
			vehiculoServicio.crearVehiculo(new Vehiculo("Motocarro", 300, 600, 1.5, 2.5, 30, 40, "Transporte desde fincas a carreteras principales"));
			vehiculoServicio.crearVehiculo(new Vehiculo("Moto con remolque", 150, 250, 0.5, 1.0, 30, 45, "Fincas pequeñas o caminos angostos"));
			vehiculoServicio.crearVehiculo(new Vehiculo("Camioneta 4x4", 800, 1200, 3, 4, 40, 60, "Terreno rural irregular, uso versátil"));
			vehiculoServicio.crearVehiculo(new Vehiculo("Camión doble troque", 10000, 15000, 40, 50, 50, 60, "Cargas medianas a grandes"));
			vehiculoServicio.crearVehiculo(new Vehiculo("Volqueta adaptada", 5000, 12000, 25, 40, 40, 60, "Para productos a granel como yuca, plátano"));
		}

		if (rutaServicio.contarRutas() == 0) {
			rutaServicio.crearRuta(new Ruta("Santa Cruz", "Casco Urbano", 15.5, true));
			rutaServicio.crearRuta(new Ruta("Sarandelo", "Casco Urbano", 8.3, true));
			rutaServicio.crearRuta(new Ruta("Palmita", "Casco Urbano", 22.0, true));
			rutaServicio.crearRuta(new Ruta("San Nicolás", "Santa Cruz", 10.2, true));
			rutaServicio.crearRuta(new Ruta("Palo de Agua", "Palmita", 7.5, true));
			rutaServicio.crearRuta(new Ruta("El Carito", "Casco Urbano", 18.6, true));
			rutaServicio.crearRuta(new Ruta("Cotocá", "Sarandelo", 9.7, true));
			rutaServicio.crearRuta(new Ruta("Nariño", "Palmita", 12.3, true));
			rutaServicio.crearRuta(new Ruta("Loma Grande", "Santa Cruz", 16.4, true));
			rutaServicio.crearRuta(new Ruta("Los Gómez", "El Carito", 14.1, true));
		}
	}

	private void mostrarMenuPrincipal() {
		System.out.println("\n======= Menú de Transporte Agrícola en Lorica =======");
		System.out.println("1. Agregar Vehículo");
		System.out.println("2. Agregar Producto");
		System.out.println("3. Agregar Ruta");
		System.out.println("4. Optimizar Transporte");
		System.out.println("5. Ver Estadísticas y Reportes");
		System.out.println("6. Salir");
		System.out.print("Seleccione una opción: ");
	}

	private void agregarVehiculo(Scanner scanner) {
		List<Vehiculo> vehiculos = vehiculoServicio.listarTodos();

		if (!vehiculos.isEmpty()) {
			System.out.println("\nVehículos existentes:");
			for (int i = 0; i < vehiculos.size(); i++) {
				System.out.println(i + ". " + vehiculos.get(i).getTipo() + " - " + vehiculos.get(i).getUsoComun());
			}
			System.out.print("¿Desea usar uno de estos vehículos? (s/n): ");
			String usarExistente = scanner.nextLine().trim().toLowerCase();

			if (usarExistente.equals("s")) {
				System.out.print("Seleccione el índice del vehículo: ");
				int index = scanner.nextInt();
				scanner.nextLine();
				Vehiculo v = vehiculos.get(index);
				System.out.println("Vehículo seleccionado: " + v.getTipo());
				return;
			}
		}

		System.out.print("Tipo de vehículo: ");
		String tipo = scanner.nextLine();
		System.out.print("Capacidad Kg (min): ");
		double kgMin = scanner.nextDouble();
		System.out.print("Capacidad Kg (max): ");
		double kgMax = scanner.nextDouble();
		System.out.print("Capacidad m3 (min): ");
		double m3Min = scanner.nextDouble();
		System.out.print("Capacidad m3 (max): ");
		double m3Max = scanner.nextDouble();
		System.out.print("Velocidad (min): ");
		double vMin = scanner.nextDouble();
		System.out.print("Velocidad (max): ");
		double vMax = scanner.nextDouble();
		scanner.nextLine();
		System.out.print("Uso común: ");
		String uso = scanner.nextLine();

		Vehiculo nuevo = new Vehiculo(tipo, kgMin, kgMax, m3Min, m3Max, vMin, vMax, uso);
		vehiculoServicio.crearVehiculo(nuevo);
		System.out.println("Vehículo registrado correctamente.");
	}


	private void agregarProducto(Scanner scanner) {
		List<Producto> productos = productoServicio.listarTodos();

		if (!productos.isEmpty()) {
			System.out.println("\nProductos existentes:");
			for (int i = 0; i < productos.size(); i++) {
				System.out.println(i + ". " + productos.get(i).getNombre());
			}
			System.out.print("¿Desea usar uno de estos productos? (s/n): ");
			String usarExistente = scanner.nextLine().trim().toLowerCase();

			if (usarExistente.equals("s")) {
				System.out.print("Seleccione el índice del producto: ");
				int index = scanner.nextInt();
				scanner.nextLine();
				Producto producto = productos.get(index);
				System.out.println("Producto seleccionado: " + producto.getNombre());
				return;
			}
		}

		System.out.print("Nombre del nuevo producto: ");
		String nombre = scanner.nextLine();

		System.out.println("Tipos de carga válidos:");
		for (Producto.TipoCarga tipo : Producto.TipoCarga.values()) {
			System.out.println("- " + tipo.name());
		}

		System.out.print("Seleccione tipo de carga: ");
		String tipoInput = scanner.nextLine().trim().toUpperCase();

		Producto.TipoCarga tipoCarga;
		try {
			tipoCarga = Producto.TipoCarga.valueOf(tipoInput);
		} catch (IllegalArgumentException e) {
			System.out.println("Tipo de carga inválido. Operación cancelada.");
			return;
		}

		System.out.print("Características: ");
		String caracteristicas = scanner.nextLine();

		Producto nuevo = new Producto(nombre, tipoCarga, caracteristicas);
		productoServicio.crearProducto(nuevo);
		System.out.println("Producto registrado correctamente.");
	}

	private void agregarRuta(Scanner scanner) {
		List<Ruta> rutas = rutaServicio.listarHabilitadas();

		if (!rutas.isEmpty()) {
			System.out.println("\nRutas existentes:");
			for (int i = 0; i < rutas.size(); i++) {
				Ruta r = rutas.get(i);
				System.out.println(i + ". " + r.getOrigen() + " -> " + r.getDestino() + " (" + r.getDistanciaKm() + " km)");
			}
			System.out.print("¿Desea usar una de estas rutas? (s/n): ");
			String usarExistente = scanner.nextLine().trim().toLowerCase();

			if (usarExistente.equals("s")) {
				System.out.print("Seleccione el índice de la ruta: ");
				int index = scanner.nextInt();
				scanner.nextLine();
				Ruta r = rutas.get(index);
				System.out.println("Ruta seleccionada: " + r.getOrigen() + " -> " + r.getDestino());
				return;
			}
		}

		System.out.print("Origen: ");
		String origen = scanner.nextLine();
		System.out.print("Destino: ");
		String destino = scanner.nextLine();
		System.out.print("Distancia (km): ");
		double distancia = scanner.nextDouble();
		scanner.nextLine();

		Ruta nueva = new Ruta(origen, destino, distancia, true);
		rutaServicio.crearRuta(nueva);
		System.out.println("Ruta registrada correctamente.");
	}

    public static String formatoPesosColombianos(double valor) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "CO"));
        return "$" + nf.format(valor) + " COP";
    }

	private void optimizarTransporte(Scanner scanner) {
		List<Producto> productos = productoServicio.listarTodos();
		List<Ruta> rutas = rutaServicio.listarHabilitadas();
		List<Vehiculo> vehiculos = vehiculoServicio.listarTodos();

		if (productos.isEmpty() || rutas.isEmpty() || vehiculos.isEmpty()) {
			System.out.println("Debe registrar al menos un producto, una ruta y un vehículo.");
			return;
		}

		System.out.println("\n=== Paso 1: Seleccione el producto ===");
		for (int i = 0; i < productos.size(); i++) {
			System.out.println(i + ". " + productos.get(i).getNombre());
		}
		System.out.print("Opción: ");
		int iProducto = scanner.nextInt();
		scanner.nextLine();

		Producto producto = productos.get(iProducto);

		System.out.println("\n=== Paso 2: Seleccione la ruta ===");
		for (int i = 0; i < rutas.size(); i++) {
			Ruta r = rutas.get(i);
			System.out.println(i + ". " + r.getOrigen() + " -> " + r.getDestino() + " (" + r.getDistanciaKm() + " km)");
		}
		System.out.print("Opción: ");
		int iRuta = scanner.nextInt();
		scanner.nextLine();

		Ruta ruta = rutas.get(iRuta);

		System.out.println("\n=== Confirmación ===");
		System.out.println("Producto: " + producto.getNombre());
		System.out.println("Ruta: " + ruta.getOrigen() + " -> " + ruta.getDestino());
		System.out.print("¿Desea continuar? (s/n): ");
		String confirmar = scanner.nextLine();
		if (!confirmar.equalsIgnoreCase("s")) {
			System.out.println("Operación cancelada.");
			return;
		}

		System.out.println("\n=== Paso 3: Capacidades disponibles para este tipo de producto ===");

		// Filtrar vehículos compatibles por tipo de producto
		List<Vehiculo> vehiculosCompatiblesTipo = vehiculos.stream()
				.filter(v -> transporteServicio.esCompatibleConTipoCarga(producto, v))
				.toList();

		if (vehiculosCompatiblesTipo.isEmpty()) {
			System.out.println("No hay vehículos compatibles con el tipo de carga del producto seleccionado.");
			return;
		}

		// Calcular rangos
		double minKg = vehiculosCompatiblesTipo.stream()
				.mapToDouble(Vehiculo::getCapacidadKgMin)
				.min().orElse(0);
		double maxKg = vehiculosCompatiblesTipo.stream()
				.mapToDouble(Vehiculo::getCapacidadKgMax)
				.max().orElse(0);

		// Mostrar información al usuario
		System.out.printf("Vehículos compatibles pueden transportar entre %.0f kg y %.0f kg de %s.\n",
				minKg, maxKg, producto.getNombre());

		System.out.print("Ingrese la cantidad a transportar (kg): ");
		double cantidad = scanner.nextDouble();
		scanner.nextLine();

		// Ajuste automático de la cantidad si está fuera del rango
		if (cantidad < minKg) {
			System.out.printf("La cantidad ingresada es menor a la capacidad mínima (%.0f kg). ", minKg);
			cantidad = minKg;
			System.out.printf("Se ajustará automáticamente a %.0f kg.\n", cantidad);
		} else if (cantidad > maxKg) {
			System.out.printf("La cantidad ingresada excede la capacidad máxima (%.0f kg). ", maxKg);
			cantidad = maxKg;
			System.out.printf("Se ajustará automáticamente a %.0f kg.\n", cantidad);
		}

		// Mostrar sugerencia de vehículo
		Vehiculo sugerido = transporteServicio.sugerirVehiculo(cantidad, vehiculosCompatiblesTipo);
		System.out.printf("Vehículo sugerido: %s (Máx: %.0f kg | Uso común: %s)\n\n",
				sugerido.getTipo(), sugerido.getCapacidadKgMax(), sugerido.getUsoComun());

		System.out.println("\n=== Paso 4: Selección de vehículo compatible ===");
		List<Vehiculo> compatibles = transporteServicio.obtenerVehiculosCompatibles(producto, cantidad, ruta, vehiculos);

		if (compatibles.isEmpty()) {
			System.out.println("No hay vehículos compatibles con la carga y ruta seleccionadas.");
			return;
		}

		for (int i = 0; i < compatibles.size(); i++) {
			Vehiculo v = compatibles.get(i);
			System.out.printf("%d. %s | Capacidad Máx: %.0f kg | Velocidad Máx: %.0f km/h | Uso: %s\n",
					i, v.getTipo(), v.getCapacidadKgMax(), v.getVelocidadMax(), v.getUsoComun());
		}
		System.out.print("Seleccione el vehículo: ");
		int iVehiculo = scanner.nextInt();
		scanner.nextLine();

		Vehiculo elegido = compatibles.get(iVehiculo);
		Transporte transporte = transporteServicio.generarTransporte(producto, cantidad, ruta, elegido);
		transporteServicio.guardarTransporte(transporte);

		System.out.println("\n=== Resumen del Transporte Optimizado ===");
		System.out.println("Producto: " + producto.getNombre());
		System.out.println("Ruta: " + ruta.getOrigen() + " -> " + ruta.getDestino());
		System.out.println("Vehículo: " + elegido.getTipo());
		System.out.println("Cantidad: " + cantidad + " kg");
		System.out.println("Tiempo estimado: " + transporte.getTiempoFormateado());
		System.out.println("Costo estimado: " + formatoPesosColombianos(transporte.getCostoEstimado()));

		System.out.println("Transporte registrado con éxito.");

		System.out.print("¿Desea exportar este transporte optimizado a PDF? (s/n): ");
		String exportar = scanner.nextLine().trim().toLowerCase();
		if (exportar.equals("s")) {
			System.out.print("Ingrese el nombre del archivo (ej: transporte_optimizado.pdf): ");
			String nombreArchivo = scanner.nextLine().trim();
			exportarTransporteUnicoPDF(nombreArchivo, transporte);
		}
	}

	private void exportarTransporteUnicoPDF(String nombreArchivoBase, Transporte transporte) {
		try {
			// Formatear nombre con fecha y extensión
			String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			if (!nombreArchivoBase.toLowerCase().endsWith(".pdf")) {
				nombreArchivoBase += "_" + fechaHora + ".pdf";
			} else {
				nombreArchivoBase = nombreArchivoBase.replace(".pdf", "_" + fechaHora + ".pdf");
			}

			// Ruta del archivo en Descargas
			String userHome = System.getProperty("user.home");
			Path downloadsPath = Paths.get(userHome, "Downloads");
			if (!Files.exists(downloadsPath)) {
				Files.createDirectories(downloadsPath);
			}
			Path archivoPath = downloadsPath.resolve(nombreArchivoBase);
			String rutaCompleta = archivoPath.toAbsolutePath().toString();

			// Crear documento PDF
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(rutaCompleta));
			document.open();

			// Agregar logo si está disponible
			try {
				String logoPath = Objects.requireNonNull(
						getClass().getClassLoader().getResource("static/img/logo.jpg")
				).getPath();
				Image logo = Image.getInstance(logoPath);
				logo.scaleToFit(80, 80);
				logo.setAlignment(Image.ALIGN_CENTER);
				document.add(logo);
			} catch (Exception e) {
				System.out.println("No se pudo cargar el logo: " + e.getMessage());
			}

			// Título del reporte
			Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
			Paragraph titulo = new Paragraph("Resumen del Transporte Optimizado", tituloFont);
			titulo.setAlignment(Element.ALIGN_CENTER);
			titulo.setSpacingAfter(15);
			document.add(titulo);

			// Tabla de resumen
			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);
			table.setWidths(new float[]{2.5f, 2f, 2f, 2.5f, 2f, 2f});

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
			BaseColor headerBg = new BaseColor(0, 121, 182);

			Stream.of("Producto", "Origen", "Destino", "Vehículo", "Tiempo (HH:mm:ss)", "Costo ($)")
					.forEach(tituloColumna -> {
						PdfPCell header = new PdfPCell(new Phrase(tituloColumna, headerFont));
						header.setBackgroundColor(headerBg);
						header.setHorizontalAlignment(Element.ALIGN_CENTER);
						header.setPadding(8f);
						table.addCell(header);
					});

			// Fuente para las celdas
			Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

			// Datos del transporte
			table.addCell(new Phrase(transporte.getProducto().getNombre(), cellFont));
			table.addCell(new Phrase(transporte.getRuta().getOrigen(), cellFont));
			table.addCell(new Phrase(transporte.getRuta().getDestino(), cellFont));
			table.addCell(new Phrase(transporte.getVehiculo().getTipo(), cellFont));

			// Tiempo estimado (formateado a HH:mm:ss)
			double horas = transporte.getTiempoEstimadoHoras();
			int totalSegundos = (int) (horas * 3600);
			int h = totalSegundos / 3600;
			int m = (totalSegundos % 3600) / 60;
			int s = totalSegundos % 60;
			String tiempoFormateado = String.format("%02d:%02d:%02d", h, m, s);
			table.addCell(new Phrase(tiempoFormateado, cellFont));

			// Costo
			table.addCell(new Phrase(formatoPesosColombianos(transporte.getCostoEstimado()), cellFont));

			document.add(table);

			// Sección de Observaciones
			Font obsTituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
			Font obsContenidoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

			Paragraph obsTitulo = new Paragraph("Observaciones:", obsTituloFont);
			obsTitulo.setSpacingBefore(15);
			obsTitulo.setSpacingAfter(5);
			document.add(obsTitulo);

			String observaciones = transporte.getObservaciones();
			if (observaciones == null || observaciones.trim().isEmpty()) {
				observaciones = "No se registraron observaciones adicionales para este transporte.";
			}

			Paragraph obsContenido = new Paragraph(observaciones, obsContenidoFont);
			obsContenido.setAlignment(Element.ALIGN_JUSTIFIED);
			obsContenido.setSpacingAfter(10);
			document.add(obsContenido);

			// Pie de página
			document.add(new LineSeparator());
			Font pieFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
			String fechaGeneracion = "Generado el: " + LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
			Paragraph footer = new Paragraph(fechaGeneracion, pieFont);
			footer.setAlignment(Element.ALIGN_RIGHT);
			document.add(footer);

			document.close();
			System.out.println("PDF generado correctamente en: " + rutaCompleta);

			// Abrir el PDF automáticamente si el sistema lo soporta
			File archivoPDF = archivoPath.toFile();
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(archivoPDF);
				System.out.println("PDF abierto automáticamente.");
			}

		} catch (Exception e) {
			System.out.println("Error al generar PDF: " + e.getMessage());
		}
	}


	private void mostrarEstadisticasGenerales() {
		long totalVehiculos = vehiculoServicio.contarVehiculos();
		long totalProductos = productoServicio.contarProductos();
		long totalRutas = rutaServicio.contarRutas();
		List<Transporte> transportes = transporteServicio.listarTodos();

		System.out.println("\n === Estadísticas Generales ===");
		System.out.println("Total de vehículos registrados: " + totalVehiculos);
		System.out.println("Total de productos registrados: " + totalProductos);
		System.out.println("Total de rutas registradas: " + totalRutas);
		System.out.println("Total de transportes realizados: " + transportes.size());

		if (!transportes.isEmpty()) {
			double tiempoPromedio = transportes.stream().mapToDouble(Transporte::getTiempoEstimadoHoras).average().orElse(0);
			double costoPromedio = transportes.stream().mapToDouble(Transporte::getCostoEstimado).average().orElse(0);
			System.out.printf("Tiempo promedio de transporte: %.2f horas%n", tiempoPromedio);
            System.out.println("Costo promedio de transporte: " + formatoPesosColombianos(costoPromedio));

        }

		System.out.print("\n¿Desea exportar el historial a PDF? (s/n): ");
		Scanner scanner = new Scanner(System.in);
		String exportar = scanner.nextLine().trim().toLowerCase();
		if (exportar.equals("s")) {
			System.out.print("Ingrese el nombre del archivo (ej: historial_transportes.pdf): ");
			String nombreArchivo = scanner.nextLine().trim();
			exportarTransportesPDF(nombreArchivo);
		}

	}

	private void exportarTransportesPDF(String nombreArchivoBase) {
        List<Transporte> transportes = transporteServicio.listarTodos();

        if (transportes.isEmpty()) {
            System.out.println("No hay transportes para exportar.");
            return;
        }

        try {
            // Nombre de archivo con fecha/hora
            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            if (!nombreArchivoBase.toLowerCase().endsWith(".pdf")) {
                nombreArchivoBase += "_" + fechaHora + ".pdf";
            } else {
                nombreArchivoBase = nombreArchivoBase.replace(".pdf", "_" + fechaHora + ".pdf");
            }

            // Ruta de descarga
            String userHome = System.getProperty("user.home");
            java.nio.file.Path downloadsPath = java.nio.file.Paths.get(userHome, "Downloads");
            if (!java.nio.file.Files.exists(downloadsPath)) {
                java.nio.file.Files.createDirectories(downloadsPath);
            }
            java.nio.file.Path archivoPath = downloadsPath.resolve(nombreArchivoBase);
            String rutaCompleta = archivoPath.toAbsolutePath().toString();

            // Crear PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(rutaCompleta));
            document.open();

            // Logo
            try {
                String logoPath = Objects.requireNonNull(getClass().getClassLoader().getResource("static/img/logo.jpg")).getPath();
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(80, 80);
                logo.setAlignment(Image.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                System.out.println("No se pudo cargar el logo: " + e.getMessage());
            }

            // Título central con fuente grande y negrita
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("Historial de Transportes", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(15);
            document.add(titulo);

            // Tabla
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{2.5f, 2f, 2f, 2.5f, 2f, 2f}); // Mejora distribución

            // Encabezado con color de fondo
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
            BaseColor headerBg = new BaseColor(0, 121, 182); // Azul claro

            Stream.of("Producto", "Origen", "Destino", "Vehículo", "Tiempo (HH:mm:ss)", "Costo ($)")
                    .forEach(tituloColumna -> {
                        PdfPCell header = new PdfPCell(new Phrase(tituloColumna, headerFont));
                        header.setBackgroundColor(headerBg);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setPadding(8f);
                        table.addCell(header);
                    });

            // Filas de contenido
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (Transporte t : transportes) {
                table.addCell(new Phrase(t.getProducto().getNombre(), cellFont));
                table.addCell(new Phrase(t.getRuta().getOrigen(), cellFont));
                table.addCell(new Phrase(t.getRuta().getDestino(), cellFont));
                table.addCell(new Phrase(t.getVehiculo().getTipo(), cellFont));

                // Formato HH:mm:ss
                double horas = t.getTiempoEstimadoHoras();
                int totalSegundos = (int) (horas * 3600);
                int h = totalSegundos / 3600;
                int m = (totalSegundos % 3600) / 60;
                int s = totalSegundos % 60;
                String tiempoFormateado = String.format("%02d:%02d:%02d", h, m, s);

                table.addCell(new Phrase(tiempoFormateado, cellFont));
                table.addCell(new Phrase(formatoPesosColombianos(t.getCostoEstimado()), cellFont));

            }

            document.add(table);

            // Línea separadora y pie de página con fecha/hora
            document.add(new LineSeparator());
            Font pieFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
            String fechaGeneracion = "Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            Paragraph footer = new Paragraph(fechaGeneracion, pieFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            System.out.println("PDF generado correctamente en: " + rutaCompleta);

            // Abrir PDF
            File archivoPDF = archivoPath.toFile();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoPDF);
                System.out.println("PDF abierto automáticamente.");
            } else {
                System.out.println("Tu sistema no soporta la apertura automática.");
            }

        } catch (Exception e) {
            System.out.println("Error al generar PDF: " + e.getMessage());
        }
    }

}

package com.example.TransporteProductosLorica;

import com.example.TransporteProductosLorica.Modelo.Producto;
import com.example.TransporteProductosLorica.Modelo.Ruta;
import com.example.TransporteProductosLorica.Modelo.Transporte;
import com.example.TransporteProductosLorica.Modelo.Vehiculo;
import com.example.TransporteProductosLorica.Servicio.ProductoServicio;
import com.example.TransporteProductosLorica.Servicio.RutaServicio;
import com.example.TransporteProductosLorica.Servicio.TransporteServicio;
import com.example.TransporteProductosLorica.Servicio.VehiculoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

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
		boolean continuar = true;

		while (continuar) {
			mostrarMenuPrincipal();
			int opcion = scanner.nextInt();
			scanner.nextLine(); // consumir newline

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

		Vehiculo v = new Vehiculo(tipo, kgMin, kgMax, m3Min, m3Max, vMin, vMax, uso);
		vehiculoServicio.crearVehiculo(v);
		System.out.println("Vehículo registrado correctamente.");
	}

	private void agregarProducto(Scanner scanner) {
		System.out.print("Nombre del producto: ");
		String nombre = scanner.nextLine();
		System.out.print("Tipo de carga: ");
		String tipo = scanner.nextLine();
		System.out.print("Características: ");
		String caracteristicas = scanner.nextLine();

		Producto p = new Producto(nombre, tipo, caracteristicas);
		productoServicio.crearProducto(p);
		System.out.println("Producto registrado correctamente.");
	}

	private void agregarRuta(Scanner scanner) {
		System.out.print("Origen: ");
		String origen = scanner.nextLine();
		System.out.print("Destino: ");
		String destino = scanner.nextLine();
		System.out.print("Distancia (km): ");
		double distancia = scanner.nextDouble();
		scanner.nextLine();

		Ruta ruta = new Ruta(origen, destino, distancia, true);
		rutaServicio.crearRuta(ruta);
		System.out.println("Ruta registrada correctamente.");
	}

	private void optimizarTransporte(Scanner scanner) {
		List<Producto> productos = productoServicio.listarTodos();
		List<Ruta> rutas = rutaServicio.listarHabilitadas();
		List<Vehiculo> vehiculos = vehiculoServicio.listarTodos();

		if (productos.isEmpty() || rutas.isEmpty() || vehiculos.isEmpty()) {
			System.out.println("Debe registrar al menos un producto, una ruta y un vehículo.");
			return;
		}

		System.out.println("Seleccione el producto:");
		for (int i = 0; i < productos.size(); i++) {
			System.out.println(i + ". " + productos.get(i).getNombre());
		}
		int iProducto = scanner.nextInt();

		System.out.println("Seleccione la ruta:");
		for (int i = 0; i < rutas.size(); i++) {
			System.out.println(i + ". " + rutas.get(i).getOrigen() + " -> " + rutas.get(i).getDestino());
		}
		int iRuta = scanner.nextInt();

		System.out.print("Cantidad a transportar (kg): ");
		double cantidad = scanner.nextDouble();
		scanner.nextLine();

		Producto producto = productos.get(iProducto);
		Ruta ruta = rutas.get(iRuta);

		Transporte transporte = transporteServicio.sugerirTransporteOptimizado(producto, cantidad, ruta, vehiculos);
		transporteServicio.guardarTransporte(transporte);

		System.out.println("\n=== Transporte Optimizado ===");
		System.out.println("Vehículo: " + transporte.getVehiculo().getTipo());
		System.out.println("Ruta: " + ruta.getOrigen() + " -> " + ruta.getDestino());
		System.out.println("Producto: " + producto.getNombre());
		System.out.println("Cantidad transportada: " + cantidad + " kg");
		System.out.println("Tiempo estimado: " + transporte.getTiempoEstimadoHoras() + " h");
		System.out.println("Costo estimado: $" + transporte.getCostoEstimado());
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
			System.out.printf("Costo promedio de transporte: $%.2f%n", costoPromedio);
		}
	}
}

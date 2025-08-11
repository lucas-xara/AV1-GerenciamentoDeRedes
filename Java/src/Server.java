import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(12345);
        System.out.println("Waiting for client to connect...");
        Socket client = server.accept();
        System.out.println("Client connected!");

        BufferedReader entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter saida = new PrintWriter(client.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);

        int serverHp = 3;
        int serverAmmo = 0;
        int clientHp = 3;
        int clientAmmo = 0;

        System.out.print("Enter your name: ");
        String serverName = scanner.nextLine();

// Recebe nome do cliente
        String clientName = entrada.readLine();

// Envia nome do servidor para cliente
        saida.println(serverName);

        while (true) {
            clearScreen();

            System.out.println("Waiting for other player's move...");

            // 1) Receive client's action
            String clientAction = entrada.readLine();
            if (clientAction == null) break;

            // 2) Server chooses action
            String serverAction = chooseAction(scanner, serverHp, serverAmmo);

            // 3) Update server status
            if (serverAction.equals("reload") && serverAmmo < 1) serverAmmo++;
            if (serverAction.equals("shoot") && serverAmmo > 0) serverAmmo--;

            // 4) Update client status
            if (clientAction.equals("reload") && clientAmmo < 1) clientAmmo++;
            if (clientAction.equals("shoot") && clientAmmo > 0) clientAmmo--;

            // 5) Calculate damage to server
            if (clientAction.equals("shoot") && !serverAction.equals("defend")) {
                serverHp--;
            }

            // 6) Calculate damage to client
            if (serverAction.equals("shoot") && !clientAction.equals("defend")) {
                clientHp--;
            }

            // 7) Send data to client
            saida.println(serverAction);
            saida.println(serverHp);
            saida.println(serverAmmo);

            // 8) Show full HUD
            System.out.println(serverName + " chose: " + serverAction);
            System.out.println(clientName + " chose: " + clientAction);
            System.out.println("Your HP:      " + getHpBar(serverHp) + "  |  Ammo: " + getAmmoBar(serverAmmo));
            System.out.println(clientName + " HP:     " + getHpBar(clientHp) + "  |  Ammo: " + getAmmoBar(clientAmmo));
            System.out.println("--------------------------------------------------");

            // 9) End game if server died
            if (serverHp <= 0) {
                System.out.println("You lost!");
                break;
            }

            if (clientHp <= 0) {
                System.out.println("You won!");
                break;
            }

            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }

        entrada.close();
        saida.close();
        client.close();
        server.close();
    }

    public static String chooseAction(Scanner scanner, int hp, int ammo) {
        while (true) {
            clearScreen();

            System.out.println("################# CHOOSE YOUR ACTION #################");
            System.out.println("##                                                  ##");
            System.out.println("##    1 - RELOAD      2 - DEFEND     3 - SHOOT      ##");
            System.out.println("##                                                  ##");
            System.out.println("######################################################");
            System.out.println("HP: " + getHpBar(hp) + "  |  Ammo: " + getAmmoBar(ammo));
            System.out.print("Your turn! ");

            String input = scanner.nextLine();

            if (input.equals("1") || input.equalsIgnoreCase("reload")) return "reload";
            if (input.equals("2") || input.equalsIgnoreCase("defend")) return "defend";
            if (input.equals("3") || input.equalsIgnoreCase("shoot")) return "shoot";

            System.out.println("Invalid option! Press Enter and try again.");
            scanner.nextLine();
        }
    }

    public static String getAmmoBar(int ammo) {
        return (ammo == 1) ? "▮" : "▯";
    }

    public static String getHpBar(int hp) {
        switch (hp) {
            case 3: return "▮▮▮";
            case 2: return "▮▮▯";
            case 1: return "▮▯▯";
            default: return "▯▯▯";
        }
    }

    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}

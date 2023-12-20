//import eventColection.ScheduleManagerImp;
import scheduleManager.Event;
import scheduleManager.ScheduleManager;

import java.util.Scanner;

import weeklyColection.ScheduleManagerImp;

public class Main {
    public static void main(String[] args) {
        ScheduleManagerImp scheduleManagerImp = new ScheduleManagerImp();

        //Ucitavanje fajla
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scheduleManagerImp.loadScheduleFromFile())
                break;
            System.out.println("Da li zelite ponovo da pokusate? (Y/N)");
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                System.exit(0);
            }

        }

        int glavniIzbor;
        int trenutniMeni = 0;
        int prethodniMeni = 0;
        // Glavni meni
        do {
            // Glavni meni
            System.out.println("Izaberite opciju:");
            System.out.println("1) Pregled(Pretrazivanje) rasporeda");
            System.out.println("2) Snimanje rasporeda u fajlove");
            System.out.println("3) Manipulacija rasporedom");
            System.out.println("4) Prekid rada");
            glavniIzbor = Integer.parseInt(scanner.nextLine());

            prethodniMeni = trenutniMeni;
            trenutniMeni = glavniIzbor;
            switch (trenutniMeni) {

                case 1: // Meni za pregled rasporeda
                    int pregledIzbor;
                    do {
                        System.out.println("Izaberite opciju za pregled:");
                        System.out.println("1) Provera da li je termin zauzet ili slobodan");
                        System.out.println("2) Izlistavanje slobodnih termina");
                        System.out.println("3) Izlistavanje zauzetih termina");
                        System.out.println("4) Pretraga po vezanim podacima");
                        System.out.println("5) Vracanje na prethodni meni");
                        System.out.println("6) Prekid rada");
                        pregledIzbor = Integer.parseInt(scanner.nextLine());
                        String input = "";
                        switch (pregledIzbor) {
                            case 1:
                                //Rg07 (u),2023-04-24,13:15,15:00
                                System.out.println("Unesite termin koji zelite da proverite u formatu $Ucionica:Datum:VremePocetka:VremeZavrsetka$\n");
                                input = scanner.nextLine();
                                if (scheduleManagerImp.doesEventExist(input)) {
                                    System.out.println("Termin postoji.\n");
                                } else {
                                    System.out.println("Termin ne postoji.\n");
                                }
                                break;
                            case 2:
                                System.out.println("Ako zelite da pretrazujete slobodne termine po nekom kriterjumu unesite ih, ako ne samo enter");
                                scheduleManagerImp.freeCriteria(scanner.nextLine());
                                break;
                            case 3:
                                scheduleManagerImp.ispis();
                                break;
                            case 4:
                                System.out.println("Unesite podatke po kojima zelite da pretrazujete u formatu $Ime podatka:podatak$\n");
                                input = scanner.nextLine();
                                for(Event x : scheduleManagerImp.searchAdditionalData(input)){
                                    System.out.println(x);
                                }
                                break;
                            case 5:
                                trenutniMeni = prethodniMeni;
                                break;
                            case 6:
                                System.exit(0);
                            default:
                                System.out.println("Nepostojeća opcija.");
                                break;
                        }
                    } while (pregledIzbor != 5);
                    break;

                case 2: // Meni za snimanje rasporeda
                    int snimanjeIzbor;
                    do {
                        System.out.println("Izaberite opciju za snimanje:");
                        System.out.println("1) Snimi fajl");
                        System.out.println("2) Vracanje na prethodni meni");
                        System.out.println("3) Prekid rada");
                        snimanjeIzbor = Integer.parseInt(scanner.nextLine());

                        switch (snimanjeIzbor) {
                            case 1:
                                scheduleManagerImp.saveSchedule();
                                break;
                            case 2:
                                trenutniMeni = prethodniMeni;
                                break;
                            case 3:
                                System.exit(0);
                            default:
                                System.out.println("Nepostojeća opcija.");
                                break;
                        }
                    } while (snimanjeIzbor != 2);
                    break;

                case 3: // Meni za manipulaciju rasporedom
                    int manipulacijaIzbor;
                    do {
                        System.out.println("Izaberite opciju za manipulaciju:");
                        System.out.println("1) Dodavanje novog termina");
                        System.out.println("2) Brisanje termina");
                        System.out.println("3) Premestanje termina");
                        System.out.println("4) Vracanje na prethodni meni");
                        System.out.println("5) Prekid rada");
                        manipulacijaIzbor = Integer.parseInt(scanner.nextLine());
                        String input = "";
                        switch (manipulacijaIzbor) {
                            case 1:
                                System.out.println("Unesite termin koji zelite da dodate u formatu $Ucionica:Datum:VremePocetka:VremeZavrsetka$\n");
                                input = scanner.nextLine();
                                scheduleManagerImp.addEvent(input);
                                break;
                            case 2:
                                System.out.println("Unesite termin koji zelite da obrisete u formatu $Ucionica:Datum:VremePocetka:VremeZavrsetka$\n");
                                input = scanner.nextLine();
                                scheduleManagerImp.removeEvent(input);
                                break;
                            case 3:
                                System.out.println("Unesite termin koji zelite da izmenite u formatu $Ucionica:Datum:VremePocetka:VremeZavrsetka$\n");
                                input = scanner.nextLine();
                                scheduleManagerImp.updateEvent(input);
                                break;
                            case 4:
                                trenutniMeni = prethodniMeni;
                                break;
                            case 5:
                                System.exit(0);
                            default:
                                System.out.println("Nepostojeća opcija.");
                                break;
                        }
                    } while (manipulacijaIzbor != 4);
                    break;

                case 4: // Prekid rada
                    System.exit(0);

                default:
                    System.out.println("Nepostojeća opcija.");
                    break;
            }
        } while (trenutniMeni != 4);
    }
}
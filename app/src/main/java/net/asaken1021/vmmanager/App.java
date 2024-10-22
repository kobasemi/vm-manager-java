package net.asaken1021.vmmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.libvirt.LibvirtException;

import net.asaken1021.vmmanager.util.*;
import net.asaken1021.vmmanager.util.vm.VMDisk;
import net.asaken1021.vmmanager.util.vm.VMDomain;
import net.asaken1021.vmmanager.util.vm.VMGraphics;
import net.asaken1021.vmmanager.util.vm.VMNetworkInterface;
import net.asaken1021.vmmanager.util.vm.VMRamUnit;
import net.asaken1021.vmmanager.util.vm.VMVideo;
import net.asaken1021.vmmanager.util.vm.networkinterface.InterfaceType;
import net.asaken1021.vmmanager.util.vm.video.VideoType;

public class App {
    public static void main(String[] args) {
        Map<String, String> argsMap = parseArgument(args);

        if (argsMap.containsKey("web-api")) {
            if (argsMap.containsKey("uri")) {
                // web api func with uri
            } else {
                // web api func without uri
            }
        } else {
            if (argsMap.containsKey("uri")) {
                String uri = argsMap.get("uri");
                if (uri.equals("")) {
                    printError("--uri オプションが渡されましたが，URIの指定がありません．デフォルトを使用します");
                    uri = "qemu:///system";
                }
                cliApp(uri);
            } else {
                cliApp("qemu:///system");
            }
        }
    }

    private static void cliApp(String uri) {
        VMManager vmm;
        int select = 0;
        Scanner scanner = new Scanner(System.in);

        List<String> vmNames;

        String vmName;
        int vmCpus;
        long vmRam;
        List<VMDisk> vmDisks;
        List<VMNetworkInterface> vmNetworkInterfaces;
        VMGraphics vmGraphics;
        VMVideo vmVideo;

        VMDomain domain;
        
        try {
            vmm = new VMManager(uri);
        } catch (ConnectException e) {
            printError(e.getLocalizedMessage());
            scanner.close();
            return;
        }

        while (select >= 0) {
            vmNames = new ArrayList<String>();

            vmName = "";
            vmCpus = 0;
            vmRam = 0;
            vmDisks = new ArrayList<VMDisk>();
            vmNetworkInterfaces = new ArrayList<VMNetworkInterface>();

            printLine();

            System.out.println("メニュー");
            System.out.println("仮想マシンの作成:  1");
            System.out.println("仮想マシンの一覧:  2");
            System.out.println("仮想マシンの情報:  3");
            System.out.println("仮想マシンの削除:  4");
            System.out.println("仮想マシンの操作:  5");
            System.out.println("終了            : -1");
            System.out.print("選択肢を入力 > ");

            try {
                select = scanner.nextInt();

                printLine();

                switch (select) {
                    case 1:
                        System.out.println("仮想マシン作成");

                        System.out.print("- 仮想マシン名 > ");
                        vmName = scanner.next();

                        System.out.print("- CPU数 > ");
                        vmCpus = scanner.nextInt();

                        System.out.print("- RAM(MiB) > ");
                        vmRam = scanner.nextLong() * 1024;

                        System.out.println("- 仮想ディスクの追加");
                        vmDisks = createVmDisks(scanner);

                        System.out.println("- ネットワークインターフェイスの追加");
                        vmNetworkInterfaces = createVmNetworkInterfaces(scanner);
                        
                        vmVideo = new VMVideo(VideoType.VIDEO_VIRTIO);
                        vmGraphics = new VMGraphics("vnc", -1);
                        
                        domain = vmm.createVm(vmName, vmCpus, vmRam, vmDisks, vmNetworkInterfaces, vmGraphics, vmVideo);
                        System.out.println("仮想マシン " + domain.getVmName() + " を作成しました");
                        break;
                    case 2:
                        System.out.println("仮想マシンの一覧");
                        vmNames = vmm.getVmNames();
                        for (String name : vmNames) {
                            System.out.println("- " + name);
                        }
                        break;
                    case 3:
                        System.out.print("仮想マシン名 > ");
                        vmName = scanner.next();

                        System.out.println("仮想マシン情報");

                        domain = vmm.getVm(vmName);

                        System.out.println("- 仮想マシン名  : " + domain.getVmName());
                        System.out.println("- 電源状態      : " + domain.getVmStateString());
                        System.out.println("- CPU数         : " + domain.getVmCpus());
                        System.out.println("- RAM(MiB)      : " + domain.getVmRamSize(VMRamUnit.RAM_MiB));
                        System.out.println("- 仮想ディスク  : ");
                        for (VMDisk disk : domain.getVmDisks()) {
                            System.out.println("- - ファイルパス: " + disk.getSourceFile());
                            System.out.println("- - ディスク情報: " + disk.getDevice() + ", "
                                + disk.getDriverType() + ", " + disk.getTargetDev() + ", " + disk.getTargetBus());
                        }
                        System.out.println("- ネットワーク  :");
                        for (VMNetworkInterface iface : domain.getVmNetworkInterfaces()) {
                            System.out.println("- - MACアドレス : " + iface.getMacAddress());
                            System.out.println("- - タイプ      : " + iface.getInterfaceType().getTypeText());
                            System.out.println("- - ソース      : " + iface.getSource());
                        }
                        System.out.println("- グラフィックス:");
                        System.out.println("- - 接続タイプ  : " + domain.getVmGraphics().getGraphicsType());
                        System.out.println("- 画面出力      :");
                        System.out.println("- - 出力デバイス: " + domain.getVmVideo().getType().getText());
                        break;
                    case 4:
                        System.out.print("仮想マシン名 > ");
                        vmName = scanner.next();
                        vmm.deleteVm(vmName);
                        System.out.println("仮想マシンを削除しました");
                        break;
                    case 5:
                        System.out.print("仮想マシン名 > ");
                        vmName = scanner.next();
                        controlVm(scanner, vmm, vmName);
                }
            } catch (DomainCreateException | DomainLookupException | DomainDeleteException | FileNotFoundException
            | TypeNotFoundException | InterfaceNotFoundException | DomainStartException | DomainStopException e) {
                printError(e.getLocalizedMessage());
            } catch (InputMismatchException e) {
                printLine();
                printError("入力に誤りがあります");
                scanner = new Scanner(System.in);
            }
        }

        scanner.close();
        try {
            vmm.disconnect();
        } catch (LibvirtException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> parseArgument(String[] args) {
        Map<String, String> parsedArgs = new HashMap<String, String>();

        int parseFlag = 0;
        String parsedValueDest = "";

        for (String arg : args) {
            if (parseFlag == 0) {
                switch (arg) {
                    case "--uri":
                        parsedArgs.put("uri", "");
                        parsedValueDest = "uri";
                        parseFlag = 1;
                        break;
                    case "--web-api":
                        parsedArgs.put("web-api", "");
                        break;
                }
            } else {
                parsedArgs.put(parsedValueDest, arg);
                parseFlag = 0;
                parsedValueDest = "";
            }
        }

        return parsedArgs;
    }

    private static void printLine() {
        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    private static void printError(String message) {
        System.err.println("エラー: " + message);
    }

    private static List<VMDisk> createVmDisks(Scanner scanner) throws FileNotFoundException {
        List<VMDisk> vmDisks = new ArrayList<VMDisk>();
        boolean addDisk = true;
        String filePath, fileType, diskType, diskDev, diskBus, select;

        while (addDisk) {
            System.out.print("- - 仮想ディスクファイルの絶対パス > ");
            filePath = scanner.next();

            System.out.print("- - 仮想ディスクファイルの種類 [qcow2/raw] > ");
            fileType = scanner.next();

            System.out.print("- - 仮想ディスクの種類 [disk/cdrom] > ");
            diskType = scanner.next();

            System.out.print("- - 仮想ディスクのデバイス名 [sdX/vdX] > ");
            diskDev = scanner.next();

            System.out.print("- - 仮想ディスクのバスタイプ [virtio/sata/scsi] > ");
            diskBus = scanner.next();

            vmDisks.add(new VMDisk(diskType, "file", "qemu", fileType, filePath, diskDev, diskBus));

            System.out.print("- さらに仮想ディスクを追加しますか? [y/n] > ");
            select = scanner.next();

            if (select.equalsIgnoreCase("n")) {
                addDisk = false;
            }
        }

        return vmDisks;
    }

    private static List<VMNetworkInterface> createVmNetworkInterfaces(Scanner scanner)
    throws TypeNotFoundException, InterfaceNotFoundException {
        List<VMNetworkInterface> vmNetworkInterfaces = new ArrayList<VMNetworkInterface>();
        boolean addInterface = true;
        String macAddress, source, model, type, select;

        while (addInterface) {
            System.out.print("- - MACアドレス (\"0\"ならランダム) > ");
            macAddress = scanner.next();

            System.out.print("- - 接続方法 [network/bridge] > ");
            type = scanner.next();

            System.out.print("- - 接続先 (ネットワークやブリッジの名前) > ");
            source = scanner.next();

            System.out.print("- - インターフェイスモデル [virtio/e1000/e1000e] > ");
            model = scanner.next();

            if (macAddress.equals("0")) {
                macAddress = "";
            }

            vmNetworkInterfaces.add(new VMNetworkInterface(macAddress, source, model, InterfaceType.getTypeByString(type)));

            System.out.print("- さらにインターフェイスを追加しますか? [y/n] > ");
            select = scanner.next();

            if (select.equalsIgnoreCase("n")) {
                addInterface = false;
            }
        }

        return vmNetworkInterfaces;
    }

    private static void controlVm(Scanner scanner, VMManager vmm, String name)
    throws DomainLookupException, DomainStartException, DomainStopException {
        int select;

        VMDomain domain = vmm.getVm(name);

        System.out.println("- 操作を選択");
        System.out.println("- 起動: 1");
        System.out.println("- 強制停止: 2");
        System.out.print("- 選択肢を入力 > ");
        select = scanner.nextInt();

        switch (select) {
            case 1:
                vmm.startVm(domain.getVmName());
                System.out.println("仮想マシン " + domain.getVmName() + " を起動しました");
                break;
            case 2:
                vmm.stopVm(domain.getVmName());
                System.out.println("仮想マシン " + domain.getVmName() + " を強制停止しました");
                break;
            default:
                System.out.println("無効な選択です");
                break;
        }
    }
}

package net.asaken1021.vmmanager;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
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
        VMManager vmm;
        int select = 0;
        Scanner scanner = new Scanner(System.in);

        List<String> vmNames;

        String vmName;
        int vmCpus;
        long vmRam;
        String vmDiskPath;
        List<VMDisk> vmDisks = new ArrayList<VMDisk>();
        List<VMNetworkInterface> vmNetworkInterfaces = new ArrayList<VMNetworkInterface>();
        VMGraphics vmGraphics;
        VMVideo vmVideo;

        VMDomain domain;
        
        try {
            vmm = new VMManager("qemu:///system");
        } catch (ConnectException e) {
            printError(e.getLocalizedMessage());
            scanner.close();
            return;
        }

        while (select >= 0) {
            printLine();

            System.out.println("MENU");
            System.out.println("Create VM    :  1");
            System.out.println("Get VMs List :  2");
            System.out.println("Get VM Info  :  3");
            System.out.println("Delete VM    :  4");
            System.out.println("Exit         : -1");
            System.out.print("Select > ");

            try {
                select = scanner.nextInt();

                printLine();

                switch (select) {
                    case 1:
                        System.out.print("VM Name > ");
                        vmName = scanner.next();
                        System.out.print("VM Cpus > ");
                        vmCpus = scanner.nextInt();
                        System.out.print("VM Ram Size (MiB) > ");
                        vmRam = scanner.nextLong() * 1024;
                        System.out.print("VM Disk path > ");
                        vmDiskPath = scanner.next();
                        vmDisks.add(new VMDisk("disk", "file", "qemu", "qcow2", vmDiskPath, "vda", "virtio"));
                        vmNetworkInterfaces.add(new VMNetworkInterface(null, "virbr0", "virtio", InterfaceType.IF_BRIDGE));
                        vmVideo = new VMVideo(VideoType.VIDEO_VIRTIO);
                        vmGraphics = new VMGraphics("vnc", -1);
                        vmm.createVm(vmName, vmCpus, vmRam, vmDisks, vmNetworkInterfaces, vmGraphics, vmVideo);
                        break;
                    case 2:
                        vmNames = vmm.getVmNames();
                        for (String name : vmNames) {
                            System.out.println(name);
                        }
                        break;
                    case 3:
                        System.out.print("VM Name > ");
                        vmName = scanner.next();
                        domain = vmm.getVm(vmName);
                        System.out.println("VM Name : " + domain.getVmName());
                        System.out.println("vCPUs   : " + domain.getVmCpus());
                        System.out.println("RAM(MiB): " + domain.getVmRamSize(VMRamUnit.RAM_MiB));
                        System.out.println("Disks   : ");
                        for (VMDisk disk : domain.getVmDisks()) {
                            System.out.println("  Disk Path: " + disk.getSourceFile());
                            System.out.println("  Disk Type: " + disk.getType());
                        }
                        System.out.println("Network :");
                        for (VMNetworkInterface iface : domain.getVmNetworkInterfaces()) {
                            System.out.println("  Interface Mac Address: " + iface.getMacAddress());
                            System.out.println("  Interface Type       : " + iface.getInterfaceType().getText());
                            System.out.println("  Interface Source     : " + iface.getSource());
                        }
                        System.out.println("Graphics:");
                        System.out.println("  Graphics Type: " + domain.getVmGraphics().getGraphicsType());
                        System.out.println("Video   :");
                        System.out.println("  Video Type: " + domain.getVmVideo().getType().getText());
                        break;
                    case 4:
                        System.out.print("VM Name > ");
                        vmName = scanner.next();
                        vmm.deleteVm(vmName);
                        System.out.println();
                        break;
                }
            } catch (DomainCreateException | DomainLookupException | DomainDeleteException | FileNotFoundException e) {
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

    private static void printLine() {
        for (int i = 0; i < 50; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    private static void printError(String message) {
        System.err.println("エラー: " + message);
    }
}

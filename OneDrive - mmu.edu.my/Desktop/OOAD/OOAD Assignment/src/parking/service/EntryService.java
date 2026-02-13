package parking.service;

import parking.data.DataCenter;
import parking.model.*; // 导入队友写好的所有类
import java.util.List;
import java.util.ArrayList;
import parking.model.SpotStatus; // <--- 加上这一行

public class EntryService {

    /**
     * 1. 创建车辆 注意：这里必须使用队友定义的 parking.model.Car 等类
     */
    public Vehicle createVehicle(String plate, String typeStr) {
        if (plate == null || plate.trim().isEmpty()) {
            return null;
        }

        // 确保车牌大写，防止 abc-123 和 ABC-123 被当成两辆车
        String cleanPlate = plate.trim().toUpperCase();

        // 根据下拉菜单的 String，实例化队友的类
        switch (typeStr) {
            case "Car":
                return new Car(cleanPlate);
            case "Motorcycle":
                return new Motorcycle(cleanPlate);
            case "SUV":
                return new SUV(cleanPlate);
            case "Handicapped":
                return new HandicappedVehicle(cleanPlate);
            default:
                return null;
        }
    }

    /**
     * 2. 查找可用车位 直接调用队友 DataCenter 写好的方法
     */
    public List<ParkingSpot> findAvailableSpots(Vehicle v) {
        try {
            // DataCenter 应该有一个静态方法 getAvailableSpotsForVehicle
            // 如果队友的方法名不一样（比如 getSpots），请在这里修改
            return DataCenter.getAvailableSpotsForVehicle(v);
        } catch (Exception e) {
            System.err.println("Error in DataCenter: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // 返回空列表防止卡死
        }
    }

    /**
     * 3. 停车并生成票据
     */
    public Ticket parkVehicle(Vehicle v, String spotId) {
        // 1. 找车位对象
        ParkingSpot spot = DataCenter.findSpotById(spotId);
        
        // 2. 直接停车
        if (spot != null) {
            
            // 这一步会把车放进数据库
            DataCenter.parkVehicle(v, spot);
            
            // --- 修复点：根据最新的报错修改 ---
            // 队友的 Ticket 需要: (String plate, String spotId, LocalDateTime time)
            // 所以我们从对象里把这三个数据提取出来传进去
            
            String plate = v.getLicensePlate();
            String assignedSpotId = spot.getSpotId();
            java.time.LocalDateTime entryTime = v.getEntryTime();
            
            // 使用正确的方式创建 Ticket
            Ticket ticket = new Ticket(plate, assignedSpotId, entryTime); 
            
            // 保存票据
            DataCenter.addTicket(ticket);
            
            return ticket;
        }
        return null;
    }
}

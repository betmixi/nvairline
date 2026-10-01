import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface ServiceLink {
  icon: string;
  label: string;
  description: string;
  routerLink: string;
}

/** Trang tổng hợp các dịch vụ bổ trợ có thể mua thêm cho vé đã đặt. */
@Component({
  standalone: true,
  selector: 'jhi-services-hub',
  imports: [CommonModule, RouterLink],
  templateUrl: './services-hub.html',
  styleUrl: './services-hub.scss',
})
export default class ServicesHubComponent {
  readonly services: ServiceLink[] = [
    { icon: '💺', label: 'Nâng hạng ghế', description: 'Đổi sang hạng ghế cao hơn cho vé đã mua', routerLink: '/upgrade-seat' },
    { icon: '🧳', label: 'Hành lý trả trước', description: 'Mua thêm hành lý ký gửi, giá rẻ hơn tại sân bay', routerLink: '/baggage' },
    { icon: '🛍️', label: 'Mua sắm', description: 'Nước hoa, rượu, quà tặng miễn thuế', routerLink: '/shopping' },
    { icon: '🏨', label: 'Khách sạn & Tour', description: 'Đặt phòng khách sạn và tour tham quan', routerLink: '/hotel-tour' },
    { icon: '🛡️', label: 'Bảo hiểm', description: 'Bảo hiểm du lịch cho chuyến bay của bạn', routerLink: '/insurance' },
    { icon: '🧩', label: 'Dịch vụ khác', description: 'Ưu tiên làm thủ tục, phòng chờ, bữa ăn, wifi', routerLink: '/other-services' },
  ];
}

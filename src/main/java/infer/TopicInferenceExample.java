package infer;

public class TopicInferenceExample {
    public static void main(String[] args){
        TopicInference tf = new TopicInference("data/topicword.csv");
        System.out.println(tf.getTopic("mua Mỹ_phẩm chăm sóc da",0.2)); //cutoff can rat nho
        System.out.println(tf.getTopic("Kênh thông_tin số 1 về bất_động_sản tại Việt_Nam: mua_bán nhà_đất, cho_thuê nhà_đất, văn_phòng, căn_hộ, biệt_thự, chung_cư.",0.2));
        System.out.println(tf.getTopic("Bài xã_luận đó đánh_đồng Tài_sản hợp_pháp và tài_sản vi_phạm pháp_luật. ",0.2));
        System.out.println(tf.getTopic("CÁCH NHẬN BIẾT LỢN MÁN NGON Lợn mán là loại lợn được người dân_tộc Mường , Mông .. Chăn_nuôi trong điều_kiện tự_nhiên , thả rông tự_nhiên tự tìm_kiếm thức_ăn cây_cỏ hoặc được người dân_tộc cho ăn bằng ngô , khoai , sắn tự_nhiên trên núi đồi nên lợn mán thường nhỏ , thịt chắc , nhiều nạc , ít mỡ , mỡ thường mầu trắng_tinh và thơm ngọt tự_nhiên , đặc_biệt bì rất giòn và ngon . Với điều_kiện sống hoang_dã , lợn mán có thân dài , mõm nhọn , chân bé , tai nhỏ , lông cứng và dài . Lợn mán được ưa_chuộng hơn hẳn bởi thịt lợn ngon , dai giòn và dễ chế_biến thành nhiều món khác nhau ( lợn hấp , lợn quay , lợn mán xào lá móc mật , lòng dồi hấp , nướng … ) , thích_hợp cho bữa cơm hàng ngày hoặc những buổi tiệc , liên_hoan , tụ_tập bạn_bè , sum_vầy bên người_thân . Nhằm đáp_ứng nhu_cầu của khách hàng , Chúng_tôi chuyên bán_buôn bán_lẻ các loại lợn mán được chăn_thả tự_nhiên để phục_vụ cho những người thích lựa_chọn các nguyên_liệu thịt sạch cho bữa cơm gia_đình , đặc_biệt chỉ giết_mổ trong ngày",0.2));

    }
}

class ContactsModel {
  String name = "";
  String phone = "";

  ContactsModel();

  ContactsModel.withNamePhone(this.name, this.phone);

  ContactsModel.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    phone = json['phone'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['name'] = name;
    data['phone'] = phone;
    return data;
  }
}

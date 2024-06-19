class BraintreeCreditCardRequest {
  BraintreeCreditCardRequest({
    required this.cardNumber,
    required this.expirationMonth,
    required this.expirationYear,
    required this.cvv,
    this.cardholderName,
  });
  String cardNumber;
  String expirationMonth;
  String expirationYear;
  String cvv;
  String? cardholderName;

  Map<String, dynamic> toJson() => {
        'cardNumber': cardNumber,
        'expirationMonth': expirationMonth,
        'expirationYear': expirationYear,
        'cvv': cvv,
        'cardholderName': cardholderName,
      };
}
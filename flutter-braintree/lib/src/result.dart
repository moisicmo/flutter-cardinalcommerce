class BraintreePaymentMethodNonce {
  const BraintreePaymentMethodNonce({
    required this.nonce,
    required this.typeLabel,
    required this.description,
    required this.isDefault,
    this.paypalPayerId,
  });

  factory BraintreePaymentMethodNonce.fromJson(dynamic source) {
    return BraintreePaymentMethodNonce(
      nonce: source['nonce'],
      typeLabel: source['typeLabel'],
      description: source['description'],
      isDefault: source['isDefault'],
      paypalPayerId: source['paypalPayerId'],
    );
  }
  final String nonce;
  final String typeLabel;
  final String description;
  final bool isDefault;
  final String? paypalPayerId;
}

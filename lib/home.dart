import 'package:flutter/material.dart';
import 'package:flutter_braintree/flutter_braintree.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static String tokenizationKey = dotenv.env['TOKENIZATION_KEY']??'';

  void showNonce(BraintreePaymentMethodNonce nonce) {
    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Payment method nonce:'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            Text('Nonce: ${nonce.nonce}'),
            const SizedBox(height: 16),
            Text('Type label: ${nonce.typeLabel}'),
            const SizedBox(height: 16),
            Text('Description: ${nonce.description}'),
          ],
        ),
      ),
    );
  }

  void sendNonceToServer(String nonce) async {
    final response = await http.post(
      Uri.parse('http://${dotenv.env['HOST']}/api/process-payment'),
      body: {'payment_method_nonce': nonce},
    );

    if (response.statusCode == 200) {
      showDialog(
        context: context,
        builder: (_) => const AlertDialog(
          title: Text('Pago exitoso'),
          content: Text('El pago se procesó correctamente.'),
        ),
      );
    } else {
      showDialog(
        context: context,
        builder: (_) => const AlertDialog(
          title: Text('Error en el pago'),
          content: Text('Hubo un problema al procesar el pago.'),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Braintree example app'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: () async {
                final request = BraintreeCreditCardRequest(
                  cardNumber: '4111111111111111',
                  expirationMonth: '12',
                  expirationYear: '2026',
                  cvv: '123',
                );
                final result = await Braintree.tokenizeCreditCard(
                  tokenizationKey,
                  request,
                );
                if (result != null) {
                  showNonce(result);
                  sendNonceToServer(result.nonce);
                }
              },
              child: const Text('TOKENIZE CREDIT CARD'),
            ),
          ],
        ),
      ),
    );
  }
}

from kafka import KafkaProducer
import json, os

producer = KafkaProducer(bootstrap_servers='localhost:29092',
                         value_serializer=lambda m: json.dumps(m).encode('utf-8'))

def produceDonations():
    with open('donation_requests.json', 'r', encoding='utf-8') as donation_requests_file:
        donation_requests = json.load(donation_requests_file)

    for request in donation_requests:
        producer.send('solicitud-donaciones', value=request)

def produceOffers():
    with open('donation_offers.json', 'r', encoding='utf-8') as donation_offers_file:
        donation_offers = json.load(donation_offers_file)

    for offer in donation_offers:
        producer.send('oferta-donaciones', value=offer)

def produceEvents():
    with open('solidarity_events.json', 'r', encoding='utf-8') as solidarity_events_file:
        solidarity_events = json.load(solidarity_events_file)

    for event in solidarity_events:
        producer.send('eventos-solidarios', value=event)

while True:
    print("\nProducir mensajes:")
    print("1. Solicitudes de donaciones")
    print("2. Ofertas de donaciones")
    print("3. Eventos solidarios")
    print("4. Enviar todo y salir")
    print("5. Salir")
    option = int(input(""))

    match option:
        case 1:
            produceDonations()
            os.system('cls')
            print("Solicitudes de donaciones enviadas")
        case 2:
            produceOffers()
            os.system('cls')
            print("Ofertas de donaciones enviadas")
        case 3:
            produceEvents()
            os.system('cls')
            print("Eventos solidarios enviados")
        case 4:
            produceDonations()
            produceOffers()
            produceEvents()
            os.system('cls')
            print("Todo enviado")
            break
        case 5:
            break
        case _:
            print("Opción inválida")

producer.flush() 
producer.close() 
/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)"

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.helloworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfloat32LE;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.IllegalName;
import hla.rti1516e.exceptions.RTIexception;

public class HelloWorld extends NullFederateAmbassador {
	
    public static final org.slf4j.Logger log = LoggerFactory.getLogger(HelloWorld.class);


	protected String settingsDesignator;
	protected String myCountry;
	private float myPopulation = (float) 100.0;
	private int numberOfCycles = 1000;
	private float growthRate = 1.0003f;

	private RTIambassador _rtiAmbassador;
	private InteractionClassHandle _messageId;
	private ParameterHandle _parameterIdText;
	private ParameterHandle _parameterIdSender;
	private ObjectInstanceHandle _countryId;
	private AttributeHandle _attributeIdName;
	private AttributeHandle _attributeIdPopulation;

	private volatile boolean _reservationComplete;
	private volatile boolean _reservationSucceeded;
	private final Object _reservationSemaphore = new Object();

	private static final String FEDERATION_NAME = "HelloWorld";
	private EncoderFactory _encoderFactory;

	private final Map<ObjectInstanceHandle, Country> _knownObjects = new HashMap<ObjectInstanceHandle, Country>();
	private final Map<String, HLAfloat32LE> countryPopulations = new HashMap<String, HLAfloat32LE>();

	public static final String SETTINGS_DESIGNATOR_ID = "SETTINGS_DESIGNATOR";
	public static final String SETTINGS_DESIGNATOR_DEFLT = "crcAddress=localhost:8989";
	public static final String FEDERATE_NAME_ID = "FEDERATE_NAME";
	public static final String FEDERATE_NAME_DEFLT = "A";
	public static final String POPULATION_SIZE_ID = "POPULATION";
	public static final float POPULATION_SIZE_DEFLT = 10;
	public static final String CYCLES_ID = "CYCLES";
	public static final int CYCLES_DEFLT = 1000000;

	private static class Country {
		private final String _name;

		Country(final String name) {
			this._name = name;
		}

		@Override
		public String toString() {
			return this._name;
		}
	}

	private static boolean getEnvironmentSettings(HelloWorld hw) {
		hw.settingsDesignator = System.getenv(SETTINGS_DESIGNATOR_ID);
		hw.myCountry = (System.getenv(FEDERATE_NAME_ID) != null) ? System.getenv(FEDERATE_NAME_ID)
				: FEDERATE_NAME_DEFLT;
		hw.myPopulation = (System.getenv(POPULATION_SIZE_ID) != null)
				? Float.parseFloat(System.getenv(POPULATION_SIZE_ID))
				: POPULATION_SIZE_DEFLT;
		hw.numberOfCycles = (System.getenv(CYCLES_ID) != null) ? Integer.parseInt(System.getenv(CYCLES_ID))
				: CYCLES_DEFLT;
		if (hw.settingsDesignator != null)
			return true;
		else
			return false;
	}

	public static void main(final String[] args) {
		// initialization procedure:
		HelloWorld hw = new HelloWorld();
		// 1. if args given, use them (settingsDesignator [fedName [populationsize
		// [numberOfCycles]]])
		if (args.length > 0) {
			System.out.println(
					"using arguments (syntax: settingsDesignator [fedName [populationsize [numberOfCycles]]])");
			System.out.println("args: " + args.toString());
			String rtiHost = args[0];
			if (args.length > 1) {
				hw.myCountry = args[1];
			}
			if (args.length > 2) {
				hw.myPopulation = Float.parseFloat(args[2]);
			}
			if (args.length > 3) {
				hw.numberOfCycles = Integer.parseInt(args[3]);
			}
			hw.settingsDesignator = "crcAddress=" + rtiHost;
		}
		// 2. else if environment settings are given, use them
		else if (getEnvironmentSettings(hw)) {
			System.out.println("using environment settings");
			System.out.println("env: settingsDesignator = " + hw.settingsDesignator);
		}
		// 3. else request user input
		else {
			System.out.println("Enter the CRC address, such as");
			System.out.println("'localhost', 'localhost:8989', '192.168.1.62'");
			System.out.println("or when using Pitch Booster on the form");
			System.out.println("<CRC name>@<booster address>:<booster port>");
			System.out.println("such as 'MyCRCname@192.168.1.70:8688'");
			System.out.println();
			try {
				final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String rtiHost;
                System.out.print("[localhost]: ");
				rtiHost = in.readLine();
				if (rtiHost.length() == 0) {
					rtiHost = "localhost";
				}
				System.out.print("Enter your country [A]: ");
				hw.myCountry = in.readLine();
				if (hw.myCountry.isEmpty()) {
					hw.myCountry = "A";
				}

				System.out.print("Enter starting population [100]: ");
				String aString = in.readLine();
				if (aString.isEmpty() == false) {
					hw.myPopulation = Float.parseFloat(aString);
				}

				System.out.print("Enter number of cycles [1000]: ");
				String bString = in.readLine();
				if (bString.isEmpty() == false) {
					hw.numberOfCycles = Integer.parseInt(bString);
				}
				hw.settingsDesignator = "crcAddress=" + rtiHost;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		hw.run();
	}

	private HelloWorld() {
	}

	private void run() {
		try {
			try {
				final RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();
				this._rtiAmbassador = rtiFactory.getRtiAmbassador();
				this._encoderFactory = rtiFactory.getEncoderFactory();
			} catch (final Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Unable to create RTI ambassador.");
				return;
			}

			this._rtiAmbassador.connect(this, CallbackModel.HLA_IMMEDIATE, settingsDesignator);

//			final URL fddFileUrl = this.getClass().getClassLoader().getResource("HelloWorld.xml");   // thats only working for pRTI
			log.info("Searching for HelloWorld SOM File...");
			File f = new File("HelloWorld.xml");
			if (!f.exists()) {
				log.info("not found at application root, searching in HelloWorld/bin/HelloWorld.xml");
				// file not found, likely because started within docker
				f = new File("HelloWorld/bin/HelloWorld.xml");
				if (f.exists()) {
					log.info("found!");
				} else {
					log.error("no FOM file found. Application terminated.");
					System.exit(1);
				}
			}
			URL fddFileUrl = f.toURI().toURL();            
			
			System.out.println("File " + fddFileUrl.getProtocol());
			System.out.println("FOM Path: " + fddFileUrl.getPath());
			try {
				this._rtiAmbassador.createFederationExecution(FEDERATION_NAME, new URL[] { fddFileUrl },
						"HLAfloat64Time");
			} catch (final FederationExecutionAlreadyExists ignored) {
			}

			this._rtiAmbassador.joinFederationExecution(this.myCountry, FEDERATION_NAME, new URL[] { fddFileUrl });

			// Subscribe and publish interactions
			this._messageId = this._rtiAmbassador.getInteractionClassHandle("Communication");
			this._parameterIdText = this._rtiAmbassador.getParameterHandle(this._messageId, "Message");
			this._parameterIdSender = this._rtiAmbassador.getParameterHandle(this._messageId, "Sender");

			this._rtiAmbassador.subscribeInteractionClass(this._messageId);
			this._rtiAmbassador.publishInteractionClass(this._messageId);

			// Subscribe and publish objects
			final ObjectClassHandle participantId = this._rtiAmbassador.getObjectClassHandle("Country");
			this._attributeIdName = this._rtiAmbassador.getAttributeHandle(participantId, "Name");
			this._attributeIdPopulation = this._rtiAmbassador.getAttributeHandle(participantId, "Population");

			final AttributeHandleSet attributeSet = this._rtiAmbassador.getAttributeHandleSetFactory().create();
			attributeSet.add(this._attributeIdName);
			attributeSet.add(this._attributeIdPopulation);

			this._rtiAmbassador.subscribeObjectClassAttributes(participantId, attributeSet);
			this._rtiAmbassador.publishObjectClassAttributes(participantId, attributeSet);

			// Reserve object instance name and register object instance
			do {
				try {
					this._reservationComplete = false;
					this._rtiAmbassador.reserveObjectInstanceName(this.myCountry);
					synchronized (this._reservationSemaphore) {
						// Wait for response from RTI
						while (!this._reservationComplete) {
							try {
								this._reservationSemaphore.wait();
							} catch (final InterruptedException ignored) {
							}
						}
					}
					if (!this._reservationSucceeded) {
						System.out.println("Name already taken, try again.");
					}
				} catch (final IllegalName e) {
					System.out.println("Illegal name. Try again.");
				} catch (final RTIexception e) {
					System.out.println("RTI exception when reserving name: " + e.getMessage());
					return;
				}
			} while (!this._reservationSucceeded);

			this._countryId = this._rtiAmbassador.registerObjectInstance(participantId, this.myCountry);

			final HLAunicodeString nameEncoder = this._encoderFactory.createHLAunicodeString(this.myCountry);

			for (int i = 0; i < numberOfCycles; i++) {
				final AttributeHandleValueMap attributes = this._rtiAmbassador.getAttributeHandleValueMapFactory()
						.create(2);
				final HLAfloat32LE messageEncoder = this._encoderFactory.createHLAfloat32LE();
				messageEncoder.setValue(this.myPopulation);
				this.myPopulation *= this.growthRate;
				attributes.put(this._attributeIdPopulation, messageEncoder.toByteArray());
				attributes.put(this._attributeIdName, nameEncoder.toByteArray());

				this._rtiAmbassador.updateAttributeValues(this._countryId, attributes, null);
				final ParameterHandleValueMap parameters = this._rtiAmbassador.getParameterHandleValueMapFactory()
						.create(1);
				final HLAunicodeString messageEncoderString = this._encoderFactory.createHLAunicodeString();
				final String message = "Hello World from " + this.myCountry;
				messageEncoderString.setValue(message);
				parameters.put(this._parameterIdText, messageEncoderString.toByteArray());
				final HLAunicodeString senderString = this._encoderFactory.createHLAunicodeString();
				senderString.setValue(this.myCountry);
				parameters.put(this._parameterIdSender, senderString.toByteArray());

				this._rtiAmbassador.sendInteraction(this._messageId, parameters, null);

				Thread.sleep(1000);
				this.printCountryPopulations();
			}

			this._rtiAmbassador.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
			try {
				this._rtiAmbassador.destroyFederationExecution(FEDERATION_NAME);
			} catch (final FederatesCurrentlyJoined ignored) {
			}
			this._rtiAmbassador.disconnect();
			this._rtiAmbassador = null;
		} catch (final Exception e) {
			e.printStackTrace();
			try {
				System.out.println("Press <ENTER> to shutdown");
				final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				in.readLine();
			} catch (final IOException ioe) {
			}
		}
	}

	private void printCountryPopulations() {
		System.out.println("Country " + this.myCountry + " has a population of " + this.myPopulation);

		for (final Map.Entry<String, HLAfloat32LE> entry : this.countryPopulations.entrySet()) {
			System.out.println("Country " + entry.getKey() + " has a population of " + entry.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass,
			final String objectName) throws FederateInternalError {
		if (!this._knownObjects.containsKey(theObject)) {
			final Country member = new Country(objectName);
			System.out.println("[" + objectName + " has joined]");
			System.out.print("> ");
			this._knownObjects.put(theObject, member);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void receiveInteraction(final InteractionClassHandle interactionClass,
			final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering,
			final TransportationTypeHandle theTransport, final SupplementalReceiveInfo receiveInfo)
			throws FederateInternalError {
		if (interactionClass.equals(this._messageId)) {
			if (!theParameters.containsKey(this._parameterIdText)) {
				System.out.println("Bad message received: No text.");
				return;
			}
			if (!theParameters.containsKey(this._parameterIdSender)) {
				System.out.println("Bad message received: No sender.");
				return;
			}
			try {
				final HLAunicodeString messageDecoder = this._encoderFactory.createHLAunicodeString();
				final HLAunicodeString senderDecoder = this._encoderFactory.createHLAunicodeString();
				messageDecoder.decode(theParameters.get(this._parameterIdText));
				senderDecoder.decode(theParameters.get(this._parameterIdSender));
				final String message = messageDecoder.getValue();
				final String sender = senderDecoder.getValue();

				System.out.println(sender + ": " + message);
				System.out.print("> ");
				String Str2 = "Hello World from";
				if (message.regionMatches(0, Str2, 0, 16)) {
					return;
				}
			} catch (final DecoderException e) {
				System.out.println("Failed to decode incoming interaction");
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void objectInstanceNameReservationSucceeded(final String objectName) {
		synchronized (this._reservationSemaphore) {
			this._reservationComplete = true;
			this._reservationSucceeded = true;
			this._reservationSemaphore.notifyAll();
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void objectInstanceNameReservationFailed(final String objectName) {
		synchronized (this._reservationSemaphore) {
			this._reservationComplete = true;
			this._reservationSucceeded = false;
			this._reservationSemaphore.notifyAll();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag,
			final OrderType sentOrdering, final SupplementalRemoveInfo removeInfo) {
		final Country member = this._knownObjects.remove(theObject);
		if (member != null) {
			final HLAfloat32LE f = this.countryPopulations.remove(member.toString());
			System.out.println("[" + member + " has left]");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reflectAttributeValues(final ObjectInstanceHandle theObject,
			final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering,
			final TransportationTypeHandle theTransport, final SupplementalReflectInfo reflectInfo) {
		if (theAttributes.containsKey(this._attributeIdName)
				&& theAttributes.containsKey(this._attributeIdPopulation)) {
			try {
				final HLAunicodeString usernameDecoder = this._encoderFactory.createHLAunicodeString();
				usernameDecoder.decode(theAttributes.get(this._attributeIdName));
				final String memberName = usernameDecoder.getValue();
				final Country member = new Country(memberName);
				final HLAfloat32LE populationDecoder = this._encoderFactory.createHLAfloat32LE();
				populationDecoder.decode(theAttributes.get(this._attributeIdPopulation));
				final float population = populationDecoder.getValue();

				this.countryPopulations.put(memberName, populationDecoder);
			} catch (final DecoderException e) {
				System.out.println("Failed to decode incoming attribute");
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void provideAttributeValueUpdate(final ObjectInstanceHandle theObject,
			final AttributeHandleSet theAttributes, final byte[] userSuppliedTag) {
		if (theObject.equals(this._countryId) && theAttributes.contains(this._attributeIdName)) {
			try {
				final AttributeHandleValueMap attributeValues = this._rtiAmbassador.getAttributeHandleValueMapFactory()
						.create(1);
				final HLAunicodeString nameEncoder = this._encoderFactory.createHLAunicodeString(this.myCountry);
				attributeValues.put(this._attributeIdName, nameEncoder.toByteArray());
				this._rtiAmbassador.updateAttributeValues(this._countryId, attributeValues, null);
			} catch (final RTIexception ignored) {
			}
		}
	}
}

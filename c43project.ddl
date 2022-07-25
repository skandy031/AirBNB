USE
    final_project;

DROP TABLE IF EXISTS Listing, Address, Amenities, Users, Renter, Hosts, Located, Provides, Owns, Reserved;


create table Users
(
    sin        integer primary key not null,
    password   varchar(50) DEFAULT 'No Occupation',
    occupation varchar(20),
    firstName  varchar(20)         not null,
    lastName   varchar(20)         not null,
    dob        DATE                not null,
    CHECK ( sin > 0 ),
    CHECK ( dob < '2004-08-02' )
);

create table Hosts
(
    hostID integer primary key not null references Users (sin)
);

create table Renter
(
    renterID    integer primary key not null references Users (sin),
    ccNumber    integer             not null,
    expiryMonth integer             not null,
    expiryYear  integer             not null,
    cvc         integer             not null,
    CHECK ( cvc < 9999 and cvc > 0 ),
    CHECK ( expiryMonth > 0 and expiryMonth <= 12 ),
    CHECK ( expiryYear > 2022 or (expiryYear = 2022 and expiryMonth > 8)),
    CHECK ( ccNumber > 0 and ccNumber < 9999999999999999 )
);

create table Listing
(
    listID      integer     not null,
    hostID      integer     not null,
    listingType varchar(15) not null,
    longitude   double      not null,
    latitude    double      not null,
    price       double      not null,
    primary key (listID, hostID),
    foreign key (hostID) references Users (sin),
    CHECK ( latitude >= -85.0 and latitude <= 85.0 ),
    CHECK ( longitude >= -180.0 and longitude <= 180.0 )
);


create table Address
(
    addressID  integer primary key,
    streetNo   integer     not null,
    streetName varchar(30) not null,
    city       varchar(30) not null,
    province   varchar(30) not null,
    postalCode varchar(6)  not null,
    unitNo     integer,
    CHECK ( streetNo > 0 and unitNo >= 0 )
);

create table Amenities
(
    amenityID           integer not null primary key,
    --     list of booleans with amenities
    wifi                boolean not null,
    washer              boolean not null,
    ac                  boolean not null,
    heating             boolean not null,
    tv                  boolean not null,
    iron                boolean not null,
    kitchen             boolean not null,
    dryer               boolean not null,
    workspace           boolean not null,
    hairDryer           boolean not null,
    pool                boolean not null,
    parking             boolean not null,
    crib                boolean not null,
    grill               boolean not null,
    indoorFireplace     boolean not null,
    hotTub              boolean not null,
    evCharger           boolean not null,
    gym                 boolean not null,
    breakfast           boolean not null,
    smoking             boolean not null,
    beachfront          boolean not null,
    waterfront          boolean not null,
    smokeAlarm          boolean not null,
    carbonMonoxideAlarm boolean not null
);



create table Reserved
(
    hostId       integer not null,
    renterID     integer not null,
    listID       integer not null,
    startDate    DATE    not null,
    endDate      DATE    not null,
    status       boolean not null,
    primary key (hostID, renterID, listID, startDate, endDate),
    foreign key (hostId) references Hosts (hostID),
    foreign key (renterID) references Renter (renterID),
    foreign key (listID) references Listing (listID),
    price        double  not null,
    hostReview   varchar(200),
    hostScore    integer,
    renterReview varchar(200),
    renterScore  integer,
    CHECK ( hostScore <= 5 and hostScore >= 1 ),
    CHECK ( renterScore <= 5 and renterScore >= 1 )
);

create table Owns
(
    listID integer not null primary key references Listing (listID)
);

create table Located
(
    listID integer not null references Listing (listID),
    addressID integer not null references Address (addressID),
    primary key (listID, addressID)
);

create table Provides
(
    amenityID integer not null references Amenities (amenityID),
    listID    integer not null references Listing (listID),
    primary key (listID, amenityID)
);
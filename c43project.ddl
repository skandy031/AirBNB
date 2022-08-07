USE final_project;

DROP TABLE IF EXISTS Listing, Address, Amenities, Users, Renter, Hosts, Located, Provides, Owns, Reserved;

create table Users
(
    sin        integer primary key not null,
    password   varchar(50),
    occupation varchar(20) DEFAULT 'No Occupation',
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
    ccNumber    varchar(20)         not null,
    expiryMonth integer             not null,
    expiryYear  integer             not null,
    cvc         integer             not null,
    CHECK ( cvc < 1000 and cvc > 99 ),
    CHECK ( expiryMonth > 0 and expiryMonth <= 12 ),
    CHECK ( expiryYear > 2022 or (expiryYear = 2022 and expiryMonth > 8))
#     CHECK ( ccNumber > 999999999999999 and ccNumber <= 9999999999999999 )
);

create table Listing
(
    listID      integer     not null auto_increment primary key,
    listingType varchar(15) not null,
    longitude   double      not null,
    latitude    double      not null,
    price       double      not null,
    CHECK ( latitude >= -90.0 and latitude <= 90.0 ),
    CHECK ( longitude >= -180.0 and longitude <= 180.0 )
);


create table Address
(
    addressID  integer primary key auto_increment,
    streetNo   integer     not null,
    streetName varchar(30) not null,
    city       varchar(30) not null,
    province   varchar(30) not null,
    country    varchar(30) not null,
    postalCode varchar(6)  not null,
    unitNo     integer,
    CHECK ( streetNo > 0 and unitNo >= 0 )
);

create table Amenities
(
    amenityID           integer not null primary key auto_increment,
--     list of booleans with amenities
    wifi                boolean not null DEFAULT FALSE,
    washer              boolean not null DEFAULT FALSE,
    ac                  boolean not null DEFAULT FALSE,
    heating             boolean not null DEFAULT FALSE,
    tv                  boolean not null DEFAULT FALSE,
    iron                boolean not null DEFAULT FALSE,
    kitchen             boolean not null DEFAULT FALSE,
    dryer               boolean not null DEFAULT FALSE,
    workspace           boolean not null DEFAULT FALSE,
    hairDryer           boolean not null DEFAULT FALSE,
    pool                boolean not null DEFAULT FALSE,
    parking             boolean not null DEFAULT FALSE,
    crib                boolean not null DEFAULT FALSE,
    grill               boolean not null DEFAULT FALSE,
    indoorFireplace     boolean not null DEFAULT FALSE,
    hotTub              boolean not null DEFAULT FALSE,
    evCharger           boolean not null DEFAULT FALSE,
    gym                 boolean not null DEFAULT FALSE,
    breakfast           boolean not null DEFAULT FALSE,
    smoking             boolean not null DEFAULT FALSE,
    beachfront          boolean not null DEFAULT FALSE,
    waterfront          boolean not null DEFAULT FALSE,
    smokeAlarm          boolean not null DEFAULT FALSE,
    carbonMonoxideAlarm boolean not null DEFAULT FALSE
);



create table Reserved
(
    reservationID   integer not null auto_increment primary key,
    hostID          integer not null,
    renterID        integer not null,
    listID          integer not null,
    startDate       DATE    not null,
    endDate         DATE    not null,
    statusAvailable boolean not null default false,
#     primary key (hostID, renterID, listID, startDate, endDate),
    foreign key (hostID) references Hosts (hostID),
    foreign key (renterID) references Renter (renterID),
    foreign key (listID) references Listing (listID),
    price           double  not null,
    hostReview      varchar(200),
    hostScore       integer,
    renterReview    varchar(200),
    renterScore     integer,
    CHECK ( hostScore <= 5 and hostScore >= 1 ),
    CHECK ( renterScore <= 5 and renterScore >= 1 )
);

create table Owns
(
    listID integer not null primary key references Listing (listID),
    hostID integer not null references Hosts (hostID)
);

create table Located
(
    listID    integer not null references Listing (listID),
    addressID integer not null references Address (addressID),
    primary key (listID, addressID)
);

create table Provides
(
    amenityID integer not null references Amenities (amenityID),
    listID    integer not null references Listing (listID)
#     primary key (listID, amenityID)
);

